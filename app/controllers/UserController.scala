package controllers

import com.google.inject.Inject
import models.{User, UserForm}
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.{Await, Future}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.util.matching.Regex

class UserController @Inject()
(userService: UserService,
 val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val SPECIAL_CHARACTERS = "[~!@#$^%&*\\(\\)_+={}\\[\\]|;:\"'<,>.?` /\\\\-]"
  val EMAIL_BUSY_ERR_MSG = "This email address is busy"
  val PASSWORD_UPPERCASE_LETTER_ERR_MSG = "Password must have at least 5 signs"
  val PASSWORD_ONE_SPECIAL_SIGN_ERR_MSG = "Password must have at least 1 special sign"
  val PASSWORD_INVALID_SIZE_ERR_MSG = "Password must have at least 5 signs"

  var errors = mutable.HashMap[String, String]()

  def home = Action.async { implicit request =>
    userService.listAllUsers map { users =>
      Ok(views.html.user(UserForm.form, errors))
    }
  }


  def login = Action { implicit request =>
    Ok(views.html.login(UserForm.form))
  }

  def signIn() = Action {
    implicit request =>
      Ok(views.html.login(UserForm.form))
  }

  def validate(word: String): String = {
    val specialCharacters = new Regex(SPECIAL_CHARACTERS)
    word.length >= 5 match {
      case true =>
        specialCharacters.findFirstMatchIn(word) match {
          case Some(_) =>
            word.exists(_.isUpper) match {
              case true => null
              case false => return PASSWORD_UPPERCASE_LETTER_ERR_MSG
            }
          case None => return PASSWORD_ONE_SPECIAL_SIGN_ERR_MSG
        }
      case false => return PASSWORD_INVALID_SIZE_ERR_MSG
    }
  }


  def addUser() = Action.async { implicit request =>
    val userForm = UserForm.form.bindFromRequest
    userForm.fold(
      errorForm => Future.successful(Ok(views.html.user(errorForm, errors))),
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email, data.password)
        errors.clear()

        if (validate(data.password) != null)
          errors += ("passwordError" -> validate(data.password))

        if (Await.result(userService.findByEmail(data.email), 1.second) != None)
          errors += ("emailError" -> EMAIL_BUSY_ERR_MSG)

        if (errors.size == 0) {
          userService.addUser(newUser).map(res =>
            Redirect(routes.UserController.home()).flashing(Messages("flash.success") -> res)
          )
        } else Future.successful(Ok(views.html.user(userForm, errors)))
      })
  }

  def deleteUser(id: Long) = Action.async {
    implicit request =>
      userService.deleteUser(id) map {
        res =>
          Redirect(routes.UserController.home())
      }
  }

}