package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models._
import models.SurveyStatus
import models.ClassCounts

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

/** This controller handles request that pertain to the creation, deletion and modification of schoolclasses
  */
@Singleton
class SchoolClassController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    val cc: ControllerComponents,
    auth: AuthenticationController
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  // this is a deserializer needed to convert Json to SchoolClass case class and ClassTeacherCC

  implicit val schoolClassReads = Json.reads[SchoolClassCC]
  implicit val schoolClassWrites = Json.writes[SchoolClassCC]

  implicit val clascountWriter = Json.writes[ClassCounts]

  implicit val classTeacherReads = Json.reads[ClassTeacherCC]

  private val model = new SchoolClassModel(db)

  /** Create an Action to create a new SchoolClass
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `POST` request with
    * a path of `/createClass`.
    */
  def createSchoolClass(): play.api.mvc.Action[play.api.mvc.AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      request.body.asJson match {
        // since this returns an option we need to cover the case that
        // the json contains something and the case that it is empty
        case Some(content) =>
          // extract json values from JsObject
          try {
            // unfortunately this throws an exception if values are not found
            val schoolClassJs: JsValue = content("schoolClass")
            val classTeacherJs: JsValue = content("classTeacher")

            // parse the Json Values
            val schoolClassOption: JsResult[SchoolClassCC] =
              Json.fromJson[SchoolClassCC](schoolClassJs)
            val classTeacherOption: JsResult[ClassTeacherCC] =
              Json.fromJson[ClassTeacherCC](classTeacherJs)

            val sc: SchoolClassCC = schoolClassOption.get
            val ct: ClassTeacherCC = classTeacherOption.get

            val schoolClass: SchoolClassDB = SchoolClassDB(
              null, // id
              sc.className,
              sc.schoolName,
              sc.classSecret,
              sc.publicKey,
              ct.teacherSecret,
              ct.encryptedPrivateKey,
              SurveyStatus.Uninitialized
            )
            model
              .createSchoolClass(schoolClass) // returns SchoolClassCC
              .map(insertedClass => Ok(Json.toJson(insertedClass))) //return

          } catch {

            case e: java.util.NoSuchElementException =>
              Future.successful(
                UnsupportedMediaType("Wrong JSON format") //return
              )
          }

        case None => Future.successful(BadRequest("Empty Body")) //return
      }
    }

  // GET /getClass/:id/:classSecret
  // returns schoolclass as json
  def getSchoolClass(implicit
      id: Int,
      classSecret: String
  ): play.api.mvc.Action[play.api.mvc.AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val body = { () =>
        model
          .getSchoolClass(id)
          .map(returnedClass => Ok(Json.toJson(returnedClass)))
      }

      // before returning the schoolclass we need to check whether the classSecret is correct
      auth.withClassAuthentication(body)

  }

  // getClassStatus/:classId/:classSecret
  def getClassStatus(implicit
      classId: Int,
      classSecret: String
  ): play.api.mvc.Action[play.api.mvc.AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val body = { () =>
        val status: Future[Int] = model.getStatus(classId)
        status.map(s => Ok(Json.obj("status" -> s)))

      }
      auth.withClassAuthentication(body)
  }

  def getCountOfAllClasses(): play.api.mvc.Action[play.api.mvc.AnyContent] = Action.async {
    val openClasses = model.getNumOfClasses(SurveyStatus.Open)
    val closedClasses = model.getNumOfClasses(SurveyStatus.Closed)
    val calculatingClasses = model.getNumOfClasses(SurveyStatus.Calculating)
    val doneClasses = model.getNumOfClasses(SurveyStatus.Done)

    val combinedCounts: Future[ClassCounts] = 
    for {
      openCl <- openClasses
      closedCl <- closedClasses
      calcCl <- calculatingClasses
      doneCl <- doneClasses
    } yield ClassCounts(openCl, closedCl, calcCl, doneCl)
    combinedCounts.map(result => Ok(Json.toJson(result)))
    
  }
}
