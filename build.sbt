name := "deadbolt-2-usage-scala"

version := "2.5.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

organization := "be.objectify"

libraryDependencies ++= Seq(
  /*jdbc,*/
  cache,
  ws,
  specs2 % Test,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "be.objectify" %% "deadbolt-scala" % "2.5.0",
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator
