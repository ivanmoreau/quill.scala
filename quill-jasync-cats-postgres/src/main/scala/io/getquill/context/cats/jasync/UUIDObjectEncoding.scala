package io.getquill.context.cats.jasync

import java.util.UUID

trait UUIDObjectEncoding {
  this: PostgresCatsJAsyncContext[_] =>

  implicit val uuidEncoder: Encoder[UUID] = encoder[UUID](SqlTypes.UUID)

  implicit val uuidDecoder: Decoder[UUID] =
    AsyncDecoder(SqlTypes.UUID)((index: Index, row: ResultRow) =>
      row.get(index) match {
        case value: UUID => value
      }
    )
}