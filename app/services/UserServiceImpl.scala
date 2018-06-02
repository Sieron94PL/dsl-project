package services

import javax.inject.Inject
import dao.UserDAO
import models.{Book, User}

import scala.concurrent.Future

class UserServiceImpl @Inject()(userDAO: UserDAO) extends UserService {
  override def addUser(user: User): Future[String] = {
    userDAO.add(user)
  }

  override def deleteUser(id: Long): Future[Int] = {
    userDAO.delete(id)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    userDAO.get(id)
  }

  override def getBook(id: Long): Future[Option[Book]] = {
    userDAO.getBook(id)
  }

  override def listAllUsers: Future[Seq[User]] = {
    userDAO.listAll
  }

  override def findByEmail(email: String): Future[Option[User]] = {
    userDAO.findByEmail(email)
  }

  override def addBook(book: Book): Future[String] = {
    userDAO.addBook(book)
  }

  override def getUserBooks(id: Long): Future[Seq[Book]] = {
    userDAO.getUserBooks(id)
  }

  override def updateBook(book: Book): Unit = {
    userDAO.updateBook(book)
  }

  override def deleteBook(id: Long): Future[Int] = {
    userDAO.deleteBook(id)
  }
}
