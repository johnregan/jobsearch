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
import JsonHandler._
import io.circe._
import io.circe.Json
import org.http4s

class IngestionService[F[_]: Sync](repository: IngestionRepository[F])
    extends Http4sDsl[F]
    with LazyLogging
    with Http4sInstances {

  val service: HttpService[F] = {
    HttpService[F] {
      case req @ PUT -> Root / "ingest" / "language" / language / "source" / source =>
        req.as[Ingestions].flatMap { is =>
          val potentailRecords = is.jobEntries.map {
            case IngestRequest(href, title, description, salaryDescription) =>
              Ingestion(UUID.randomUUID(), title, href, description, salaryDescription, ZonedDateTime.now(), source, language)
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
      case GET -> Root / "jobs" / "language" / language =>
        Ok(repository.getIngestions(language).map(_.asJson))
      case GET -> Root / "languages" =>
        repository.getLanguages.flatMap { ls =>
          Ok(ls.asJson)
        }
    }
  }
}
