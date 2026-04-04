ThisBuild / scalaVersion := "3.8.3"
ThisBuild / organization := "org.teckhooi"

// set to Debug for compilation details (Info is default)
logLevel := Level.Info

import sbt.Keys.scalacOptions
import scala.scalanative.build.*

// defaults set with common options shown

/** Cats Effect 3.6.3 is compiled against Scala Native 0.4.x. and it only works with this version. On the other hand,
  * Cats Effect 3.7.x works with Scala Native 0.5.x. It won't work with Scala Native 0.4.x
  */
//val catsEffectVersion  = "3.6.3"  // only works with Scala Native 0.4.x. Supports single thread
val catsEffectVersion = "3.7.0" // requires Scala Native 0.5.x and above for parallel executions

ThisBuild / nativeConfig ~= { c =>
  c.withLTO(LTO.none)     // thin
    .withMode(Mode.debug) // releaseFast
    .withGC(GC.immix)     // commix
}

ThisBuild / scalacOptions ++= Seq("-no-indent")

lazy val native3_8_x = project
  .settings(
    // use triple '%' for Scala Native like Scala JS
    libraryDependencies ++= Seq("org.typelevel" %%% "cats-effect" % catsEffectVersion),
    Compile / mainClass := Some("org.teckhooi.ConcurrentRunCats"),
    name := "concurrent-run"
  )
  .enablePlugins(ScalaNativePlugin)

lazy val native3_6_x = project
  .settings(
    scalaVersion := "3.6.4",
    // use triple '%' for Scala Native like Scala JS
    libraryDependencies ++= Seq("org.typelevel" %%% "cats-effect" % catsEffectVersion),
    Compile / mainClass := Some("org.teckhooi.ConcurrentRunCats"),
    name         := "concurrent-run"
  )
  .enablePlugins(ScalaNativePlugin)
