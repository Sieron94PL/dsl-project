package models

import play.api.data.Form
import play.api.data.Forms._

case class Book(val id: Long, userId: Long, title: String, category: String)

case class BookFormData(title: String, category: String)

object BookForm {

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "category" -> nonEmptyText
    )(BookFormData.apply)(BookFormData.unapply)
  )
}
