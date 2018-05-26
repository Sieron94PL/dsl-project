package controllers

import javax.inject.Inject
import be.objectify.deadbolt.scala.DeadboltActions
import play.api.mvc.{Action, Controller}
import security.MyDeadboltHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application @Inject()(deadbolt: DeadboltActions) extends Controller {

  def echo = Action { request =>
    Ok("Welcome!").withSession(
      "connected" -> "user@gmail.com")
  }

  def session = Action { request =>
    Ok(request.session.get("connected").toString)
  }

  def index = deadbolt.WithAuthRequest()() { authRequest =>
    Future {
      Ok(views.html.index(new MyDeadboltHandler)(authRequest))
    }
  }
}