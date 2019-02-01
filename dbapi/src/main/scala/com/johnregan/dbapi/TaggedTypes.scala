package com.johnregan.dbapi
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions

object TaggedTypes {

  @newtype case class Href(url: String)
  object Href {

    implicit def encode: Encoder[Href] =
      Encoder.encodeString.contramap[Href](_.url)
    implicit def decode: Decoder[Href] = Decoder.decodeString.map(Href(_))
  }

  @newtype case class Title(value: String)
  object Title {

    implicit def encode: Encoder[Title] =
      Encoder.encodeString.contramap[Title](_.value)
    implicit def decode: Decoder[Title] = Decoder.decodeString.map(Title(_))
  }

  @newtype case class Description(value: String)
  object Description {

    implicit def encode: Encoder[Description] =
      Encoder.encodeString.contramap[Description](_.value)
    implicit def decode: Decoder[Description] = Decoder.decodeString.map(Description(_))
  }

  @newtype case class SalaryDescription(value: String)
  object SalaryDescription {

    implicit def encode: Encoder[SalaryDescription] =
      Encoder.encodeString.contramap[SalaryDescription](_.value)
    implicit def decode: Decoder[SalaryDescription] = Decoder.decodeString.map(SalaryDescription(_))
  }
}
