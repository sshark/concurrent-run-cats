package org.teckhooi

import cats.FlatMap
import cats.effect.kernel.Sync
import cats.effect.std.{Console, Random}
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.toTraverseOps

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
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
            .flatMap(delay => task[IO](x.toString, Duration(100 + delay, TimeUnit.MILLISECONDS)).start)
        )
      _       <- fiberList.map(_.joinWithNever).sequence
      endTime <- IO.realTime
      _       <- IO.println(s"Time taken to complete all tasks, ${(endTime - startTime).toMillis}ms")
    } yield ExitCode.Success

  /** The following code compiles for Scala pre-v3.7.x. Please refer to
    * https://stackoverflow.com/questions/74601647/contextbound-temporal-causes-cannot-resolve-symbol-flatmap for
    * details
    */
  private def task[F[_]: {Console, Temporal, Sync, FlatMap}]( // Method (A)
      name: String,
      delay: Duration
  ): F[Unit] = {
    val x = Sync[F].flatMap(Sync[F].delay(Thread.currentThread().getName))(startThreadName =>
      Console[F].println(s"Starting task $name on $startThreadName...")
    )
    val y = Sync[F].flatMap(x)(_ => Temporal[F].sleep(delay))
    val z = Sync[F].flatMap(y)(_ => Sync[F].delay(Thread.currentThread().getName))

    Sync[F].flatMap(z)(endThreadName =>
      Console[F].println(s"Task $name completed after ${delay.toMillis}ms on $endThreadName")
    )
  }
}
