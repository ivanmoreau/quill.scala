package io.getquill.context.cats

import com.github.jasync.sql.db.{ConcreteConnection, QueryResult, RowData}
import io.getquill.context.sql.SqlContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.util.ContextLogger
import io.getquill.{NamingStrategy, ReturnAction}
import kotlin.jvm.functions.Function1

import java.time.ZoneId
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
import scala.util.Try
import io.getquill.context.TranslateContext
import io.getquill.context.Context
import io.getquill.context.jasync.JAsyncContext
import io.getquill.context.jasync.SqlTypes

abstract class CatsJAsyncContext[D <: SqlIdiom, N <: NamingStrategy, C <: ConcreteConnection](
  val idiom: D,
  val naming: N
) extends Context[D, N]
  with TranslateContext
  with SqlContext[D, N]
    with CatsIOMonad {

        protected val dateTimeZone = ZoneId.systemDefault()

        override type ResultRow = RowData



        
    }