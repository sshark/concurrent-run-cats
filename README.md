# Concurrent Run with Scala Native And Cat Effect

Scala Native v0.5.x supports multithreading. Therefore, Cats Effect v3.7.x can run in parallel in Scala Native v0.5.x.

Scala Native v0.4.x supports single threaded model and it is incompatible with Cats Effect v3.7.x

## How-to Build Scala Native Executables
* Use `sbt nativeLink` to build an application with debug info
* Use `sbt nativeLinkReleaseFast` to build a lean application i.e. without debug info
* Either command will build both projects when it is executed in the project root.

## Details
* Make sure to use `%%%` in the Scala Native dependencies. Otherwise, Scala Native plugin will not see these dependencies
* `Compile / mainClass` must be defined in the `build.sbt`.
* `name` will be the name of the final executable. For `nativeLinkReleaseFast` release, `-release-fast` will be appended to the executable filename. 
* For earlier versions of Cats Effect before __v.3.7.x__, `-out` will be appended to the executable filename.
* When the code is compiled using Scala v3.6.x, it will runs into a `flatMap` context bound ambiguity error between `Sync` and `Temporal`. Therefore, `task(...)` is coded differently in Scala v3.6.x from v3.8.x.
