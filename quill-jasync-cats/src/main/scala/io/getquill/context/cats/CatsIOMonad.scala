package io.getquill.context.cats

import io.getquill.context.Context
import io.getquill.monad.{IOMonad, IOMonadMacro}

import cats.effect.{IO => CatsIO}

import scala.collection.compat._
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.util.{Failure, Success}
import io.getquill.Query
import io.getquill.Action
import io.getquill.ActionReturning
import io.getquill.BatchAction

trait CatsIOMonad extends IOMonad {
  this: Context[_, _] =>

  type Result[T] = CatsIO[T]

  def runIO[T](quoted: Quoted[T]): IO[RunQuerySingleResult[T], Effect.Read] = macro IOMonadMacro.runIO
  def runIO[T](quoted: Quoted[Query[T]]): IO[RunQueryResult[T], Effect.Read] = macro IOMonadMacro.runIO
  def runIO(quoted: Quoted[Action[_]]): IO[RunActionResult, Effect.Write] = macro IOMonadMacro.runIO
  def runIO[T](
    quoted: Quoted[ActionReturning[_, T]]
  ): IO[RunActionReturningResult[T], Effect.Write] = macro IOMonadMacro.runIO
  def runIO(
    quoted: Quoted[BatchAction[Action[_]]]
  ): IO[RunBatchActionResult, Effect.Write] = macro IOMonadMacro.runIO
  def runIO[T](
    quoted: Quoted[BatchAction[ActionReturning[_, T]]]
  ): IO[RunBatchActionReturningResult[T], Effect.Write] = macro IOMonadMacro.runIO

  case class Run[T, E <: Effect](f: () => Result[T]) extends IO[T, E]

  def performIO[T](io: IO[T, _], transactional: Boolean = false): Result[T] =
    io match {
      case FromTry(v) => CatsIO.fromTry(v)
      case Run(f)     => f()
      case seq @ Sequence(in, cbf) =>
        CatsIO.parSequenceN(0)(in.iterator.map(performIO(_)).toSeq).map(r => cbf.fromSpecific(r))
      case TransformWith(a, fA) =>
        performIO(a).attempt.flatMap(valueOrError => performIO(fA(valueOrError.fold(Failure(_), Success(_)))))
      case Transactional(io) =>
        performIO(io, transactional = true)
    }
}