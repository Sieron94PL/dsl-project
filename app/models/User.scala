package models

import be.objectify.deadbolt.scala.models.Subject
import play.api.data.Form
import play.api.data.Forms._

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */
case class User(val id: Long, firstName: String, lastName: String, mobile: Long, email: String, password: String) extends Subject {
  override def roles: List[SecurityRole] =
    List(SecurityRole("foo"),
      SecurityRole("bar"))

  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = id.toString
}

case class UserFormData(firstName: String, lastName: String, mobile: Long, email: String, password: String)


object UserForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobile" -> longNumber,
      "email" -> email,
      "password" -> nonEmptyText
    )(UserFormData.apply)(UserFormData.unapply)
  )
}



