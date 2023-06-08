package io.getquill.context.cats.jasync

import java.util.UUID

trait UUIDStringEncoding {
  this: PostgresCatsJAsyncContext[_] =>

  implicit val uuidEncoder: Encoder[UUID] = encoder[UUID]((v: UUID) => v.toString, SqlTypes.UUID)

  implicit val uuidDecoder: Decoder[UUID] =
    AsyncDecoder(SqlTypes.UUID)((index: Index, row: ResultRow) =>
      row.get(index) match {
        case value: String => UUID.fromString(value)
      }
    )
}