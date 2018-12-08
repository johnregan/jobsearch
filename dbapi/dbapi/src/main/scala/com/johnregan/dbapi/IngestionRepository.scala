package com.johnregan.dbapi

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.UUID

import cats.data._
import cats.effect.Effect
import doobie._
import io.circe.{Decoder, Encoder}
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object JsonHandler {
  import cats.syntax.either._

  implicit val encodeZdt: Encoder[ZonedDateTime] = Encoder.encodeString.contramap[ZonedDateTime](DateTimeFormatter.ISO_OFFSET_DATE_TIME.format)
  implicit val decodeZdt: Decoder[ZonedDateTime] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(ZonedDateTime.parse(str)).leftMap(_ => "err")
  }
}

object DoobieHandler {
  import cats._
  import cats.implicits._
  import doobie.implicits._
  import cats.implicits._
  import org.postgresql.util.PGobject
  import doobie.postgres.implicits._
  import doobie.util.meta.Meta

  implicit val mDateTimeMeta: Meta[ZonedDateTime] =
    Meta[java.sql.Timestamp].xmap(
      ts     => ZonedDateTime.ofInstant(ts.toInstant, ZoneOffset.UTC),
      ldt    => new java.sql.Timestamp(ldt.toInstant.toEpochMilli)
    )
}



case class Ingestions(jobEntries: NonEmptyList[IngestRequest])

object Ingestions {
  import JsonHandler._

  implicit val decoder: Decoder[Ingestions] = deriveDecoder
  implicit val encoder: Encoder[Ingestions] = deriveEncoder
}

case class IngestRequest(href:String,description:String)

object IngestRequest {
  import JsonHandler._

  implicit val decoder: Decoder[IngestRequest] = deriveDecoder
  implicit val encoder: Encoder[IngestRequest] = deriveEncoder
}

case class Ingestion(id: UUID, href: String, description: String, createdDate: ZonedDateTime, source: String)

object Ingestion {
  import JsonHandler._

  implicit val decoder: Decoder[Ingestion] = deriveDecoder
  implicit val encoder: Encoder[Ingestion] = deriveEncoder
}

class IngestionRepository[F[_]](xa: Transactor[F])(implicit F: Effect[F]) {
  import cats.Foldable._
  import DoobieHandler._
  import cats._
  import cats.implicits._
  import doobie.implicits._
  import doobie.postgres.implicits._

  def getAll(): fs2.Stream[F, Ingestion] = sql"""SELECT * FROM ingestions ORDER BY created_date DESC""".query[Ingestion].stream.transact(xa)

  def insert(ingestions: NonEmptyList[Ingestion]): F[Int] = Update[Ingestion]("insert into ingestions (id, href, description, created_date, source) values (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING").updateMany(ingestions).transact(xa)
}

