# Concurrent Run with Scala Native And Cat Effect

Scala Native 0.5.x supports multithreaded and Cats Effect 3.7.x supports Scala Native 0.5.x. 
Together, we can run multithread application in Scala Native with Cats Effect. 

Scala Native 0.4.x supports single threaded model and it is incompatible with Cats Effect 3.7.x

## How-to Build Scala Native Demo
* Use `sbt nativeLink` to build an application with debug info
* Use `sbt nativeLinkReleaseFast` to build a lean application i.e. without debug info

## Details
* Make sure to use `%%%` in the Scala Native dependencies. Otherwise, Scala Native plugin will not see these dependencies
* `Compile / mainClass` must be defined in the `build.sbt`
* `name` will be the name of the final executable. For `nativeLinkReleaseFast` release, `-release-fast` will be appended to the executable filename. 
* For earlier versions of Cats Effect before __v.3.7.x__, `-out` will be appended to the executable filename.
* When working with Scala v.3.6.x and below. change the method `org.teckhooi.ConcurrentRunCats.task(...)` according 
to the comment. Otherwise, the code won't compile