package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPatterns extends App {

  // method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]
  class P2PThinnava

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T: Serializer](message: T): Int
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
   // def receive(future: Future[P2PResponse]): Int  // type erasure problem
    // lots of overloads
  }

  /*
     these poses no.of problems
     1. type erasure
     2. lifting doesn't work for all overloads
          val receiveFV = receive _ // compiler will be confused at this case
     3. code duplication
     4. type inference and default args
         actor.receive(?) which one should be default don't know
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[T](magnet: MessageMagnet[T]): T = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling P2PRequest
      println("Handling P2PRequest")
      36
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling P2PResponse
      println("Handling P2PResponse")
      63
    }
  }

  class FromP2PThinnava(thinnava: P2PThinnava) extends MessageMagnet[Int] {
    override def apply(): Int = 25
  }



  println(receive(new P2PRequest))
  println(receive(new P2PResponse))
//  println(receive(new P2PThinnava))

  // 1. no more type erasure problems
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 6
  }

  println(receive(Future(new P2PRequest))) // 3
  println(receive(Future(new P2PResponse))) // 6

 //  2. lifting works with a small catch(if no type parameter)
  trait MathLib {
   def add1(x: Int): Int = x + 1
   def add1(s: String): Int = s.toInt + 1
   // lot of add1 overloads
 }

  // magnetize
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class IntMagnet(value: Int) extends AddMagnet {
    override def apply(): Int = value + 1
  }

  implicit class StringMagnet(value: String) extends AddMagnet {
    override def apply(): Int = value.toInt + 1
  }

  println(add1(3))
  println(add1("23"))
  val addFv = add1 _
  println(addFv(3))  //4
  println(addFv("23"))  // 24

  val receiveFv = receive _
 //  println(receiveFv(new P2PResponse))
 //  println(receiveFv(new P2PRequest))

  /*
    for receive method MessageMagnet[Result] has type parameter
    for add1 method AddMagnet doesn't has type parameter

    when we call  receive _ , here compiler can not identify the type of given blank argument
    when we call add1 _, here compiler the blank argument doesn't has type parameter so no compilation error
   */




  /*
     Draw backs:
      1. verbose
      2. hard to read
      3. you can't name or place default arguments
      4. call by name doesn't work correctly
   */

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // lot of overloads
  }

  // magnetizing
  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "hahhahaha"
  }

  handle(sideEffectMethod)
  //Hello, Scala
  //hahhahaha
  //Hello, Scala
  //hahhahaha

  handle {
    println("Hello, Scala")
    "hahhahaha"  // new StringHandler("hahhahaha") =>
  }
  //Hello, Scala
  //hahhahaha
  //hahhahaha

  // here Hello, Scala printed only 1 time,





}
