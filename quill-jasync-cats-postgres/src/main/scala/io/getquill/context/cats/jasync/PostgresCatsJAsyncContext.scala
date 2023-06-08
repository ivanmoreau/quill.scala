package io.getquill.context.cats.jasync

import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import com.github.jasync.sql.db.{QueryResult => DBQueryResult}
import io.getquill.ReturnAction.{ReturnColumns, ReturnNothing, ReturnRecord}
import io.getquill.util.Messages.fail
import io.getquill.{NamingStrategy, PostgresDialect, ReturnAction}

import io.getquill.context.cats.CatsJAsyncContext

import scala.jdk.CollectionConverters._
import scala.util.Try
import java.util.Date
import java.time.LocalDate
import java.util.UUID
import com.github.jasync.sql.db.RowData

class PostgresCatsJAsyncContext[N <: NamingStrategy](naming: N)
    extends CatsJAsyncContext[PostgresDialect, N, PostgreSQLConnection](PostgresDialect, naming)
    with Encoders
    with Decoders
    with UUIDObjectEncoding
     {

        override type ResultRow  = RowData
        type Session    = Unit
        type PrepareRow = Seq[Any]

        override def close: Unit =
    // nothing to close since pool is in env
    ()

    def probe(sql: String): Try[_] =
    Try(()) // need to address that

     override private[getquill] def prepareParams(statement: String, prepare: Prepare): Seq[String] =
    prepare(Nil)._2.map(prepareParam)
  
   protected def extractActionResult[O](returningAction: ReturnAction, returningExtractor: Extractor[O])(result: DBQueryResult): O =
    result.getRows.asScala
      .headOption
      .map(returningExtractor)
      .getOrElse(fail("This is a bug. Cannot extract returning value."))

   protected def expandAction(sql: String, returningAction: ReturnAction): String =
    returningAction match {
      // The Postgres dialect will create SQL that has a 'RETURNING' clause so we don't have to add one.
      case ReturnRecord => s"$sql"
      // The Postgres dialect will not actually use these below variants but in case we decide to plug
      // in some other dialect into this context...
      case ReturnColumns(columns) => s"$sql RETURNING ${columns.mkString(", ")}"
      case ReturnNothing          => s"$sql"
    }

}