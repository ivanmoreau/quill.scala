package io.getquill.context.cats

import io.getquill.NamingStrategy
import io.getquill.context.{ Context, ContextEffect, TranslateContextBase }
import io.getquill.idiom.Idiom
import cats.effect.IO

trait CatsTranslateContext extends TranslateContextBase {
  this: Context[_ <: Idiom, _ <: NamingStrategy] =>

  override type TranslateResult[T] = IO[T]

  override private[getquill] val translateEffect: ContextEffect[IO] = new ContextEffect[IO] {
    override def wrap[T](t: => T): IO[T] = IO.apply(t)
    override def push[A, B](result: IO[A])(f: A => B): IO[B] = result.map(f)
    override def seq[A, B](list: List[IO[A]]): IO[List[A]] = IO.parSequenceN(1)(list)
  }
}
