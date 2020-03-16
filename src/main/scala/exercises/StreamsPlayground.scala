package exercises

import scala.annotation.tailrec


abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](ele: B): MyStream[B]  // prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]  // concatenate two streams

  def foreach(f: A => Unit): Unit
  def map[B >: A](f: A => B): MyStream[B]
  def flatMap[B >: A](f: A => MyStream[B]): MyStream[B]
  def filter(f: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] //  takes first n elements out of the stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  def printElements: String

  def printElementsNew: String

  def force: String = "Stream(" + printElementsNew + ")"

  override def toString: String = "Stream(" + printElements + ")"

  /*
     [1,2,3].toList([])
      [2,3].toList([1])
      [3].toList(2 :: [1])
      [].toList(3 :: [2,1])
      [].toList([3,2,1])

   */

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
  }
}


object EmptyStream extends MyStream[Nothing]{

  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](ele: B): MyStream[B] = new Cons[B](ele, this)
  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  def foreach(f: Nothing => Unit): Unit = ()
  def map[B >: Nothing](f: Nothing => B): MyStream[B] = this
  def flatMap[B >: Nothing](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(f: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = EmptyStream

  def printElements: String = ""

  def printElementsNew: String = ""

}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {

  def isEmpty: Boolean = false

  /*
      here hd and tl are overriding as val cuz these values may be used many times in code
      instead of computing everytime when they called assigning to val will make compute only once and can reused
      in Streams only tail always in lazy so overriding as lazy val
   */
  override val head: A = hd
  override lazy val tail: MyStream[A] = tl // call by need


  /*
     val s = new Cons(1, EmptyStream)
     val prepended = 1 #:: s = new Cons(1, s)
   */
  def #::[B >: A](ele: B): MyStream[B] = new Cons[B](ele, this)

  /*
     here tail is lazy evaluated and
          tail ++ anotherStream is call by name in Cons class
          so this will be evaluated when it called
          call by name + lazy => call by need
   */
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = {
    new Cons(head, tail ++ anotherStream)
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
  /*
    s = new Cons(1, ?)
    mapped = s.map(_ + 1)
    new Cons(2, ?) // new Cons(2, s.tail.map(_ + 1)
    mapped.tail when we use it will force to compute
   */
  def map[B >: A](f: A => B): MyStream[B] = {
    new Cons[B](f(head), tail.map(f))  // still preserves lazy evaluation
  }

  def flatMap[B >: A](f: A => MyStream[B]): MyStream[B] = {
    f(head) ++ tail.flatMap(f)
    //tail.flatMap(f) ++ f(head)
  }
  def filter(f: A => Boolean): MyStream[A] = {
    if (f(head)) new Cons[A](head, tail.filter(f))
    else tail.filter(f)  // still preserves lazy evaluation
  }

  def take(n: Int): MyStream[A] = {
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1))  // still preserves lazy evaluation
  }

  def printElements: String = {
    s"${head}, ?"
  }
/*
  def printElementsNew: String = {
    @tailrec
    def forceRec(s: MyStream[A], acc: String): String  = {
      if (s.isEmpty) acc
      else if (s.tail.isEmpty)  s.head + acc
      else forceRec(s.tail,  "," + s.head + acc)
    }
    forceRec(this, "")
  }
*/

  def printElementsNew: String = {
    if (tl.isEmpty) "" + hd
    else  hd + "," + tl.printElementsNew
  }

}



object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = {
    new Cons(start, MyStream.from(generator(start))(generator) )
  }
}


object StreamsPlayground  extends App {

  val naturals = MyStream.from(1)(x => x + 1)
  println(naturals) // Stream(1, ?)
  println(naturals.tail) // Stream(2, ?)
  println(naturals.head)  // 1
  println(naturals.tail.head)  // 2
  println(naturals.tail.tail.head)  // 3

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head) // 0

  // startFrom0.take(10000).foreach(println)

  // map
  println(startFrom0.map(x => x * 2).take(100).toList()) // List(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20,....)

  // flatMap
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList()) // List(0, 1, 1, 2, 2, 3, 3, 4, 4, 5)

  // filter
  println(startFrom0.filter(_ < 10).take(10).toList()) // List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

  val first10 = naturals.filter(_ < 11).take(10)
  println(first10)  // Stream(1, ?)
  println(first10.tail)  // Stream(2, ?)
  println(first10.force)  // Stream(,1,2,3,4,5,6,7,8,9,10)

  // println(naturals.filter(_ < 10).take(10).force) // throws stackOVerFlowError, cuz we are trying to get 10 elements
  // but Stream has 9 elements, it still tries as Streams are infinite

  println(naturals.filter(_ < 10).take(9).take(20).force) // Stream(,1,2,3,4,5,6,7,8,9)
  // still works cuz already we got finite Stream from there even
  // if we try to access more elements it won't throw error


  /**
    * Exercise
    * 1.Stream of fibonacci numbers
    * 2.Stream of prime numbers with eratosthenes' sieve
    * */

  /*
     fibonacci(1,1)
     new Cons(1, fibonacci(1, 1 + 1))
     new Cons(1, new Cons(1, fibonacci(2, 1 + 2)))
     new Cons(1, new Cons(1, new Cons(2, fibonacci(3, 2 + 3))))
   */


  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = {
    new Cons[BigInt]( first, fibonacci( second, first + second ) )
  }

  println(fibonacci(1,1).take(50))  // Stream(1, ?)
  println(fibonacci(1,1).take(50).toList()) // List(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987,....)


  /*
    Eratosthenes' sieve prime formulae: starts from 2 all naturals
    [2,3,4,5,6,7,8,9,10,...]
    filter out all numbers divisible by 2 // excluding 2
    [2,3,5,7,9,11,13,15,...]
    filter out all numbers divisible by 3 // excluding 3
    [2,3,5,7,11,13,17,19,23,25,29,31,35,37,39,...]
    filter out all numbers divisible by 5  // excluding 5
    [2,3,5,7,11,13,17,19,23,29,31,37,39,...]
   */

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenes(numbers.tail.filter(x => x % numbers.head != 0)))
  }

  val naturalsNew = MyStream.from(2)(_ + 1)

  println(eratosthenes(naturalsNew).take(100).toList())
  // List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,.....541)
  // 541 is the 100th prime number






}
