package com.johnregan.dbapi

import java.time.ZonedDateTime
import java.util.UUID

import cats.effect.Sync
import cats.syntax.all._
import com.johnregan.dbapi.Ingestion._
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class HelloWorldService[F[_]: Sync](repository: IngestionRepository[F]) extends Http4sDsl[F] with LazyLogging {

  val service: HttpService[F] = {
    HttpService[F] {
      case req @ PUT -> Root / "ingest" / source =>
        req.as[Ingestions].flatMap { is =>
          val potentailRecords = is.jobEntries.map {
            case IngestRequest(href, description) =>
              Ingestion(UUID.randomUUID(), href, description, ZonedDateTime.now(), source)
          }

          repository.insert(potentailRecords).attempt.flatMap {
            case Right(rowsUpdated) if rowsUpdated > 0 =>
              Created(s"Created $rowsUpdated new entries in the database".asJson)
            case Right(_) => NoContent()
            case Left(error) =>
              logger.error(s"unexpected error when writing source:$source to the database", error)
              BadRequest("Error encountered when writing to the database")
          }
        }
      case GET -> Root / "jobs" / "stream" =>
        Ok(repository.getAll().map(_.asJson))
    }
  }
}
