val Http4sVersion = "0.18.21"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val ScalaLogging = "3.9.0"
val FlywayVersion = "5.2.4"
val DoobieVersion = "0.5.3"

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
      "io.circe" %% "circe-generic" % "0.9.3",
      "io.circe" %% "circe-literal" % "0.9.3",
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLogging,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "com.zaxxer" % "HikariCP" % "3.2.0",
      "org.postgresql" % "postgresql" % "42.2.2"
    ),
    scalastyleFailOnError := true,

    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

