package lectures.part3concurrency

import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  //1. parallel collections

  val parList = List(1,2,3).par
  // operations on par collections done by multiple threads at the same time

  // another way of instantiating
  val aParVector = ParVector[Int](1,2,3)


  /*
     Seq
     Vector
     Array
     Map - Hash, Trie
     Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val lst = (1 to 10000000).toList
  val serailTime = measure {
    lst.map(_ + 1)
  }
  println("serial time: " + serailTime)   // serial time: 5308

  val parallelTime = measure{
    lst.par.map(_ + 1)
  }
  println("parallel time: " + parallelTime)  // parallel time: 3404

  /*
    Map-reduce model
    - split the elements into chunks - Splitter
    - operation
    - recombine - Combiner

   */

  // map, flatMap, filter, foreach, reduce, fold
  // map, flatMap, filter and foreach are pretty safe

  // reduce and fold are not always, they are non-associative operators
  println(List(1,2,3).reduce(_ - _))      // -4
  println(List(1,2,3).par.reduce(_ - _))  // 2

  // synchronization
  var sum = 0
  List(1,2,3).par.foreach(x => sum += x)
  println(sum) // race-conditions, sometimes we may get sum value different cuz of different threads running









}
