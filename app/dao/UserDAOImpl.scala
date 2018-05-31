package dao

import javax.inject.Inject
import models.{Book, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class UserDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends UserDAO {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  class UserTable(tag: Tag)
    extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def mobile = column[Long]("mobile")

    def email = column[String]("email")

    def password = column[String]("password")

    override def * =
      (id, firstName, lastName, mobile, email, password) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[UserTable]

  class BookTable(tag: Tag)
    extends Table[Book](tag, "book") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def title = column[String]("title")

    def category = column[String]("category")

    override def * =
      (id, userId, title, category) <> (Book.tupled, Book.unapply)


    def user = foreignKey("user_id", userId, users)(_.id)
  }

  val books = TableQuery[BookTable]


  override def add(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  override def addBook(book: Book): Future[String] = {
    db.run(books += book).map(res => "Book successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  override def updateBook(book: Book) = {
    db.run(books.filter(_.id === book.id).update(book))
  }

  override def delete(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }

  override def get(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  override def getBook(id: Long): Future[Option[Book]] = {
    db.run(books.filter(_.id === id).result.headOption)
  }

  override def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  override def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }

  override def getUserBooks(id: Long): Future[Seq[Book]] = {
    db.run(books.filter(_.userId === id).result)
  }

  override def deleteBook(id: Long): Future[Int] = {
    db.run(books.filter(_.id === id).delete)
  }
}