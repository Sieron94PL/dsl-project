package security

import be.objectify.deadbolt.scala.AuthenticatedRequest
import be.objectify.deadbolt.scala.models.Subject
import javax.inject.Inject
import services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */

class MyUserlessDeadboltHandler(userService: UserService) extends MyDeadboltHandler(userService) {
  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = Future(None)
}