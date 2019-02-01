val Http4sVersion = "0.18.21"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val ScalaLogging = "3.9.0"
val FlywayVersion = "5.2.4"
val DoobieVersion = "0.5.3"
val PostgresVersion = "42.2.2"
val CirceVersion = "0.9.3"
val HikariCPVersion = "3.2.0"
val NewtypeVersion = "0.4.2"
val RefinedVersion = "0.9.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings", "-language:higherKinds")

lazy val root = (project in file("."))
  .settings(
    organization := "com.johnregan",
    name := "dbapi",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLogging,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "com.zaxxer" % "HikariCP" % HikariCPVersion,
      "org.postgresql" % "postgresql" % PostgresVersion,
      "io.estatico" %% "newtype" % NewtypeVersion,
      "eu.timepit" %% "refined" % "0.9.4"
    ),
    scalastyleFailOnError := true,
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

mainClass in assembly := Some("com.johnregan.dbapi.HelloWorldServer")
