package controllers

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.Inject
import models._
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.{Await, Future}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.matching.Regex

class UserController @Inject()
(userService: UserService, deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders,
 val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val SPECIAL_CHARACTERS = "[~!@#$^%&*\\(\\)_+={}\\[\\]|;:\"'<,>.?` /\\\\-]"
  val EMAIL_BUSY_ERR_MSG = "This email address is busy"
  val PASSWORD_UPPERCASE_LETTER_ERR_MSG = "Password must have at least 5 signs"
  val PASSWORD_ONE_SPECIAL_SIGN_ERR_MSG = "Password must have at least 1 special sign"
  val PASSWORD_INVALID_SIZE_ERR_MSG = "Password must have at least 5 signs"
  val LOGIN_ERR_MSG = "Invalid email address or password"

  var errors = mutable.HashMap[String, String]()

  /*
  Books
   */
  def userBooks = deadbolt.SubjectPresent()() { implicit request =>

    val email = request.session.get("email").get
    val user = Await.result(userService.findByEmail(email), Duration.Inf).get
    val books = Await.result(userService.getUserBooks(user.id), Duration.Inf).seq

    if (request.getQueryString("id") != None) {
      val bookId = request.getQueryString("id").get.toLong
      val userBook = Await.result(userService.getBook(bookId), Duration.Inf).get
      val bookForm = BookForm.form.fill(BookFormData(userBook.title, userBook.category))
      Future(Ok(views.html.books(books, bookForm, userBook)))
    } else {
      println(books)
      Future(Ok(views.html.books(books, BookForm.form, null)))
    }
  }

  def addBook = Action.async { implicit request =>

    val email = request.session.get("email").get
    val user = Await.result(userService.findByEmail(email), Duration.Inf).get
    val books = Await.result(userService.getUserBooks(user.id), Duration.Inf).seq

    val bookForm = BookForm.form.bindFromRequest()
    bookForm.fold(
      errorForm => Future(BadRequest(views.html.books(books, errorForm, null))),
      data => {
        val book = Book(0, user.id, data.title, data.category)
        userService.addBook(book)
        Future(Redirect(routes.UserController.userBooks()))
      }
    )
  }

  def editBook = Action.async { implicit request =>
    val email = request.session.get("email").get
    val user = Await.result(userService.findByEmail(email), Duration.Inf).get
    val books = Await.result(userService.getUserBooks(user.id), Duration.Inf).seq
    val bookForm = BookForm.form.bindFromRequest()
    bookForm.fold(
      errorForm => Future(BadRequest(views.html.books(books, errorForm, null))),
      data => {
        val bookId = request.body.asFormUrlEncoded.get("bookId")(0).toString.toLong
        val book = Book(bookId, user.id, data.title, data.category)
        userService.updateBook(book)
        Future(Redirect(routes.UserController.userBooks()))
      }
    )
  }

  def deleteBook(id: Long) = deadbolt.SubjectPresent()() {
    implicit request =>
      userService.deleteBook(id) map {
        res =>
          Redirect(routes.UserController.userBooks())
      }
  }

  /*
  Login
   */
  def login = deadbolt.SubjectNotPresent()() { implicit request =>
    Future(Ok(views.html.login(LoginForm.form, null)))
  }

  def logout = deadbolt.SubjectPresent()() { implicit request =>
    Future(Ok(views.html.login(LoginForm.form, null)).withNewSession)
  }

  def signIn() = Action.async { implicit request =>
    val loginForm = LoginForm.form.bindFromRequest()
    loginForm.fold(
      errorForm => Future(BadRequest(views.html.login(errorForm, LOGIN_ERR_MSG))),
      data => {
        if (Await.result(userService.findByEmail(data.email), Duration.Inf) != None) {
          val user = Await.result(userService.findByEmail(data.email), Duration.Inf).get
          if (user.password == data.password) {
            Future(Redirect(routes.UserController.userBooks()).withSession("email" -> data.email))
          }
          else {
            Future(Unauthorized(views.html.login(loginForm, LOGIN_ERR_MSG)))
          }
        } else {
          Future(Unauthorized(views.html.login(loginForm, LOGIN_ERR_MSG)))
        }
      })
  }

  /*
  Registration
   */
  def registration = deadbolt.SubjectNotPresent()() { implicit request =>
    Future(Ok(views.html.registration(RegistrationForm.form, errors)))
  }

  def addUser() = Action.async { implicit request =>
    val registrationForm = RegistrationForm.form.bindFromRequest

    registrationForm.fold(
      errorForm => Future(BadRequest(views.html.registration(errorForm, errors))),
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email, data.password)
        errors.clear()

        if (passwordVerify(data.password) != null)
          errors += ("passwordError" -> passwordVerify(data.password))

        if (Await.result(userService.findByEmail(data.email), 1.second) != None)
          errors += ("emailError" -> EMAIL_BUSY_ERR_MSG)

        if (errors.size == 0) {
          userService.addUser(newUser).map(res =>
            Redirect(routes.UserController.login()).flashing(Messages("flash.success") -> res)
          )
        } else Future.successful(Ok(views.html.registration(registrationForm, errors)))
      })
  }

  def passwordVerify(password: String): String = {
    val specialCharacters = new Regex(SPECIAL_CHARACTERS)

    password.length >= 5 match {
      case true =>
        specialCharacters.findFirstMatchIn(password) match {
          case Some(_) =>
            password.exists(_.isUpper) match {
              case true => null
              case false => return PASSWORD_UPPERCASE_LETTER_ERR_MSG
            }
          case None => return PASSWORD_ONE_SPECIAL_SIGN_ERR_MSG
        }
      case false => return PASSWORD_INVALID_SIZE_ERR_MSG
    }
  }

  def deleteUser(id: Long) = deadbolt.SubjectNotPresent()() {
    implicit request =>
      userService.deleteUser(id) map {
        res =>
          Redirect(routes.UserController.registration())
      }
  }

}