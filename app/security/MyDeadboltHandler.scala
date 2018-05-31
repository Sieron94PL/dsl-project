package security

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import dao.UserDAO
import javax.inject.Inject
import models.User
import play.api.mvc.{Request, Result, Results}
import services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */
class MyDeadboltHandler @Inject()(userService: UserService, dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {
    None
  }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {
    None
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    if (request.session.get("email").nonEmpty) {
      val email = request.session.get("email").get
      val user = Await.result(userService.findByEmail(email), 1.second).get
      Future(Some(new User(user.id, user.firstName, user.lastName, user.mobile, user.email, user.password)))
    } else {
      Future(None)
    }
  }

  def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future {
      Results.Forbidden(views.html.accessFailed())
    }
  }
}