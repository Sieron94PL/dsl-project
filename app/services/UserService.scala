package services

import com.google.inject.ImplementedBy
import models.{Book, User}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def addUser(user: User): Future[String]

  def addBook(book: Book): Future[String]

  def getUser(id: Long): Future[Option[User]]

  def getBook(id: Long): Future[Option[Book]]

  def deleteUser(id: Long): Future[Int]

  def deleteBook(id: Long): Future[Int]

  def getUserBooks(id: Long) : Future[Seq[Book]]

  def updateBook(book: Book)

  def listAllUsers: Future[Seq[User]]

  def findByEmail(email: String) : Future[Option[User]]
}
