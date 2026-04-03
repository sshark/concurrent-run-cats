enablePlugins(ScalaNativePlugin)
scalaVersion := "3.8.3"

organization := "org.teckhooi"
name         := "concurrent-run"

// set to Debug for compilation details (Info is default)
logLevel := Level.Info

// import to add Scala Native options
import scala.scalanative.build._

// defaults set with common options shown
nativeConfig ~= { c =>
  c.withLTO(LTO.none)     // thin
    .withMode(Mode.debug) // releaseFast
    .withGC(GC.immix)     // commix
}

/** Cats Effect 3.6.3 is compiled against Scala Native 0.4.x. and it only works with this version. On the other hand,
  * Cats Effect 3.7.x works with Scala Native 0.5.x. It won't work with Scala Native 0.4.x
  */
//val catsEffectVersion  = "3.6.3"  // only works with Scala Native 0.4.x. Supports single thread
val catsEffectVersion = "3.7.0" // requires Scala Native 0.5.x and above for parallel executions

// use triple '%' for Scala Native like Scala JS
libraryDependencies ++= Seq("org.typelevel" %%% "cats-effect" % catsEffectVersion)

Compile / mainClass := Some("org.teckhooi.ConcurrentRunCats")
