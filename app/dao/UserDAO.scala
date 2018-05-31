package dao

import com.google.inject.ImplementedBy
import models.{Book, User}

import scala.concurrent.Future

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO {
  def add(user: User): Future[String]

  def addBook(book: Book): Future[String]

  def get(id: Long): Future[Option[User]]

  def updateBook(book: Book)

  def getBook(id: Long): Future[Option[Book]]

  def delete(id: Long): Future[Int]

  def deleteBook(id: Long): Future[Int]

  def listAll: Future[Seq[User]]

  def getUserBooks(id: Long): Future[Seq[Book]]

  def findByEmail(email: String): Future[Option[User]]
}
