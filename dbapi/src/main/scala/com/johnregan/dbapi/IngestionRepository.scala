package com.johnregan.dbapi

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.data._
import cats.effect.Effect
import com.johnregan.dbapi.TaggedTypes.{Description, Href, SalaryDescription, Title}
import doobie._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

object JsonHandler {
  import cats.syntax.either._

  implicit val encodeZdt: Encoder[ZonedDateTime] =
    Encoder.encodeString.contramap[ZonedDateTime](DateTimeFormatter.ISO_OFFSET_DATE_TIME.format)
  implicit val decodeZdt: Decoder[ZonedDateTime] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(ZonedDateTime.parse(str)).leftMap(_ => "err")
  }
}

object DoobieHandler {
  import doobie.util.meta.Meta

//  No TypeTag available for .............
//  implicit val metaHref: Meta[Href] =
//    Meta[String].xmap(
//      href => Href(href),
//      href => href.url
//    )

  implicit val mDateTimeMeta: Meta[ZonedDateTime] =
    Meta[java.sql.Timestamp].xmap(
      ts => ZonedDateTime.ofInstant(ts.toInstant, ZoneOffset.UTC),
      ldt => new java.sql.Timestamp(ldt.toInstant.toEpochMilli)
    )
}

case class Ingestions(jobEntries: NonEmptyList[IngestRequest])
object Ingestions {

  implicit val decoder: Decoder[Ingestions] = deriveDecoder
  implicit val encoder: Encoder[Ingestions] = deriveEncoder
}

case class IngestRequest(href: Href, title:Title, description: Description, salaryDescription:SalaryDescription)
object IngestRequest {

  implicit val decoder: Decoder[IngestRequest] = deriveDecoder
  implicit val encoder: Encoder[IngestRequest] = deriveEncoder
}

case class Ingestion(id: UUID,
                     title: String,
                     href: String,
                     description: String,
                     salary:String,
                     createdDate: ZonedDateTime,
                     source: String,
                     language: String)
object Ingestion {
  import JsonHandler._

  def apply(id: UUID,
            title: Title,
            href: Href,
            description: Description,
            salary:SalaryDescription,
            createdDate: ZonedDateTime,
            source: String,
            language: String): Ingestion = new Ingestion(id, title.value, href.url, description.value, salary.value, createdDate, source, language)

  implicit val decoder: Decoder[Ingestion] = deriveDecoder
  implicit val encoder: Encoder[Ingestion] = deriveEncoder
}

class IngestionRepository[F[_]](xa: Transactor[F])(implicit F: Effect[F]) {
  import DoobieHandler._
  import doobie.implicits._
  import doobie.postgres.implicits._

  def getIngestions(language: String): fs2.Stream[F, Ingestion] =
    sql"""SELECT * FROM ingestions WHERE language = $language ORDER BY created_date DESC"""
      .query[Ingestion]
      .stream
      .transact(xa)

  def getLanguages: F[List[String]] =
    sql"""SELECT * FROM languages"""
      .query[String]
      .to[List]
      .transact(xa)

  def insert(ingestions: NonEmptyList[Ingestion]): F[Int] =
    Update[Ingestion](
      "insert into ingestions (id, title, href, description, salary, created_date, source, language) values (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"
    ).updateMany(ingestions).transact(xa)
}
