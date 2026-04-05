package org.teckhooi

import cats.FlatMap
import cats.effect.kernel.Sync
import cats.effect.std.{Console, Random}
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.toTraverseOps
import cats.syntax.flatMap.*
import cats.syntax.functor.*

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.Try

object ConcurrentRunCats extends IOApp {
  private val alphas = ('A' to 'Z').toList

  override def run(args: List[String]): IO[ExitCode] =
    for {
      numOfTasks <-
        if args.isEmpty then IO.pure(alphas.size)
        else IO.fromTry(Try(args.head.toInt).map(math.min(alphas.size, _)).recover(_ => alphas.size))
      rnd       <- Random.scalaUtilRandom[IO]
      startTime <- IO.realTime
      fiberList <- alphas
        .take(numOfTasks)
        .traverse(x =>
          rnd
            .nextIntBounded(300)
            .flatMap(delay => task[IO](x.toString, (100 + delay).millis).start)
        )
      _       <- fiberList.map(_.joinWithNever).sequence
      endTime <- IO.realTime
      _       <- IO.println(s"Time taken to complete all tasks, ${(endTime - startTime).toMillis}ms")
    } yield ExitCode.Success

  /**
   * Alternative code for Scala v.3.6.x and below. Please refer to {{native3_6_x}} project
   */
  private def task[F[_]: {Console, Temporal, Sync, FlatMap}](  // Method (A)
      name: String,
      delay: Duration
  ): F[Unit] =
    for {
      startThreadName <- Sync[F].delay(Thread.currentThread().getName)
      _ <- Console[F].println(s"Starting task $name on $startThreadName...") >> Temporal[F].sleep(delay)
      endThreadName <- Sync[F].delay(Thread.currentThread().getName)
      _ <- Console[F].println(s"Task $name completed after ${delay.toMillis}ms on $endThreadName")
    } yield ()
}
