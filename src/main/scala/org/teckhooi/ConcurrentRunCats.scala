package org.teckhooi

import cats.FlatMap
import cats.effect.kernel.Sync
import cats.effect.std.{Console, Random}
import cats.effect.{IO, IOApp, Temporal}
import cats.implicits.toTraverseOps
import cats.syntax.apply.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object ConcurrentRunCats extends IOApp.Simple {
  override def run: IO[Unit] =
    for {
      rnd <- Random.scalaUtilRandom[IO]
      f   <- ('A' to 'Z').toList.traverse(x =>
        rnd
          .nextIntBounded(300)
          .flatMap(delay => task[IO](x.toString, Duration(100 + delay, TimeUnit.MICROSECONDS)).start)
      )
      _ <- f.map(_.joinWithNever).sequence
    } yield ()

  private def task[F[_]: {Console, Temporal, Sync, FlatMap}](
      name: String,
      delay: Duration
  ): F[Unit] =
    for {
      // Sync[F] must be on its own i.e. flat mapped. It can't be joined with *>
      startThreadName <- Sync[F].delay(Thread.currentThread().getName)
      _               <- Console[F].println(s"Starting $name on $startThreadName...") *>
        Temporal[F].sleep(delay)
      endThreadName <- Sync[F].delay(Thread.currentThread().getName)
      _             <- Console[F].println(s"$name completed after ${delay.length}ms on $endThreadName")
    } yield ()
}
