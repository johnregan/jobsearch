package com.johnregan.dbapi

import cats.effect.{Effect, IO}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.Transactor
import fs2.StreamApp
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    for {
      ds <- ServerStream.dataSource[IO]
      _ <- fs2.Stream.eval(new FlywayHandler[IO](ds).performMigration)
      tx <- fs2.Stream(Transactor.fromDataSource[IO](ds))
      ec <- ServerStream.stream[IO](tx)
    } yield {
      ec
    }
}

object ServerStream {
  val hikariConfig: HikariConfig = {
    val config = new HikariConfig()
    config.setJdbcUrl("jdbc:postgresql://localhost:5432/freelancedb")
    config.setUsername("jr")
    config.setPassword("")
    config
  }

  def dataSource[F[_]: Effect](implicit F: Effect[F]): fs2.Stream[F, HikariDataSource] =
    fs2.Stream.eval(F.liftIO(IO(new HikariDataSource(hikariConfig))))

  def ingestionRepository[F[_]: Effect](xa: Transactor[F]): IngestionRepository[F] = new IngestionRepository[F](xa)

  def ingestionService[F[_]: Effect](ingestionRepository: IngestionRepository[F]): HttpService[F] =
    new IngestionService[F](ingestionRepository).service

  def stream[F[_]: Effect](xa: Transactor[F])(implicit ec: ExecutionContext): fs2.Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(ingestionService(ingestionRepository(xa)), "/")
      .serve
}
