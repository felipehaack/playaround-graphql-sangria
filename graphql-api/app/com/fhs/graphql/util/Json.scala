package com.fhs.graphql.util

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime}

import play.api.libs.json._

import scala.util.Try

object JsonUtil extends JsonEnum with JsonEither with JsonFormats with JsonConverts

trait JsonEnum {

  final def enumValueReads[E <: Enumeration](enum: E): Reads[E#Value] = Reads {
    case JsString(s) =>
      try {
        JsSuccess(enum.withName(s))
      } catch {
        case _: NoSuchElementException =>
          JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
      }
    case _ => JsError("String value expected")
  }

  final def enumIdReads[E <: Enumeration](enum: E): Reads[E#Value] = Reads {
    case JsNumber(id) =>
      try {
        JsSuccess(enum(id.intValue()))
      } catch {
        case _: NoSuchElementException =>
          JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$id'")
      }
    case _ => JsError("String value expected")
  }

  final def enumValueWrites[E <: Enumeration](): Writes[E#Value] = {
    Writes { v: E#Value => JsString(v.toString) }
  }

  final def enumIdWrites[E <: Enumeration](): Writes[E#Value] = {
    Writes { v: E#Value => JsNumber(v.id) }
  }

  final def enumValueFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumValueReads(enum), enumValueWrites())
  }

  final def enumIdFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumIdReads(enum), enumIdWrites())
  }
}

trait JsonEither {

  implicit def eitherReads[A: Reads, B: Reads]: Reads[Either[A, B]] = {
    Reads { json =>
      json.validate[A] match {
        case JsSuccess(value, path) => JsSuccess(Left(value), path)
        case JsError(aError) =>
          json.validate[B] match {
            case JsSuccess(value, path) => JsSuccess(Right(value), path)
            case JsError(bError) => JsError(JsError.merge(aError, bError))
          }
      }
    }
  }

  implicit def eitherWrites[A: Writes, B: Writes]: Writes[Either[A, B]] = {
    Writes {
      case Left(a) => Json.toJson(a)
      case Right(b) => Json.toJson(b)
    }
  }

  implicit def eitherFormat[A: Format, B: Format]: Format[Either[A, B]] = {
    Format(eitherReads, eitherWrites)
  }
}

trait JsonFormats {

  private val LocalDateTimeUTCFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
  private val LocalDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val LocalTimeUTCReads = DateTimeFormatter.ofPattern("HH:mm:ss[.SSSSSS]['Z']")
  private val LocalTimeUTCWriter = DateTimeFormatter.ofPattern("HH:mm:ss'Z'")

  implicit val LocalDateFormat = Format[LocalDate](
    Reads[LocalDate]{
      case JsString(v) => Try(LocalDate.parse(v, LocalDateFormatter)).toOption match {
        case Some(d) => JsSuccess(d)
        case None => JsError("error.expected.date.isoformat")
      }
      case _ => JsError("error.expected.date")
    },
    Writes[LocalDate](value => JsString(value.format(LocalDateFormatter)))
  )

  implicit val LocalDateTimeFormat = Format[LocalDateTime](
    Reads[LocalDateTime]{
      case JsString(v) => Try(LocalDateTime.parse(v, LocalDateTimeUTCFormatter)).toOption match {
        case Some(d) => JsSuccess(d)
        case None => JsError("error.expected.date.isoformat")
      }
      case _ => JsError("error.expected.date")
    },
    Writes[LocalDateTime](value => JsString(value.format(LocalDateTimeUTCFormatter)))
  )

  implicit val LocalTimeFormat = Format[LocalTime](
    Reads[LocalTime]{
      case JsString(v) => JsSuccess(LocalTime.parse(v, LocalTimeUTCReads))
      case _ => JsError("error.expected.date")
    },
    Writes[LocalTime](value => JsString(value.format(LocalTimeUTCWriter)))
  )

}

trait JsonConverts {

  implicit class ToJson[T: Writes](value: T) {
    def toJson: JsValue = Json.toJson(value)
  }

  implicit class FromJson(value: String) {
    def fromJson[T: Reads]: T = Json.parse(value).as[T]
  }

  implicit class RichJsObject(obj: JsObject) {
    def stripNulls: JsObject = {
      JsObject(obj.fields.filter(v => v._2 match {
        case JsNull | JsString("") => false
        case JsArray(arr) if arr.isEmpty => false
        case _ => true
      }))
    }
  }

}
