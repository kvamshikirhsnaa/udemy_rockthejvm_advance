package lectures.part1as

import scala.util.Try

object DarkSugars {
  def main(args: Array[String]): Unit = {

    // syntax sugar

    // 1. method with single parameter

    def singlArgMethod(x: Int): String = s"$x little lions"
    println(singlArgMethod(3))  // 3 little lions

    val description = singlArgMethod {
      // write some complex code
      5
    }
    println(description)  // 5 little lions

    val aTryInstance = Try {  // java's try { }
      throw new RuntimeException
    }

    List(1,2,3).map{x =>
      x + 2
    }


    // 2. single abstract method, it is available on scala 2.12 later version

    // trait or abstract class should contain single unimplemented method
    // then we can instance it using lambda function
    trait Action {
      def act(x: Int): Int
     //def eat(x: String): String  // it throws error if trait has more than 1 method
    }

    val anInstance: Action = new Action {      // here  IDE suggest convert to single abstract method
      override def act(x: Int): Int = x + 2
     // override def eat(x: String): String = s"$x $x $x"

    }
    println(anInstance)  // lectures.part1as.DarkSugars$$anon$1@7e0babb1
    println(anInstance.act(4))  // 6

    // compiler automatically converts if a trait or abstract class has single unimplemented method
    // we should specify return type as trait or class, if we didn't give return type will be normal lambda function
    val aFunkyInstance: Action = (x: Int) => x + 2
    println(aFunkyInstance)  // lectures.part1as.DarkSugars$$anonfun$1@6debcae2
    println(aFunkyInstance.act(4))   // 6

    // not giving return type Action
    val anAnonInstance = (x: Int) => x + 2  // lambda function
    println(anAnonInstance)  // lectures.part1as.DarkSugars$$$Lambda$7/1537358694@2ff4f00f
    println(anAnonInstance(4))  // 6
    // println(anAnonInstance.act(4)) // this is not type of Action so we don't get act method on that instance

    // example: Runnable (java interface for threads)
    val aThread = new Thread(new Runnable {
      override def run(): Unit = println("Hello, Scala")
    })
    println(aThread) // Thread[Thread-0,5,main]
    println(aThread.start()) //Hello, Scala () ->   on thread start() method internally calls run method
    println(aThread.run())  // Hello, Scala ()

    val aSweetThread = new Thread(() => println("sweet, Scala"))
    println(aSweetThread)  // Thread[Thread-1,5,main]
    println(aSweetThread.start())  // sweet, Scala ()
    println(aSweetThread.run())  // sweet, Scala ()

    abstract class AnAbstractType {
      def implemented: Int = 21
      def f(a: Int): Unit
    }

    val anAbstractInstance: AnAbstractType = new AnAbstractType {
      override def f(a: Int): Unit = println(a)
    }

    println(anAbstractInstance)  // lectures.part1as.DarkSugars$$anon$3@5a39699c
    anAbstractInstance.f(anAbstractInstance.implemented) // 21

    val anAbstractSuganrInstance: AnAbstractType = (a: Int) => println(a)
    println(anAbstractSuganrInstance)  // lectures.part1as.DarkSugars$$anonfun$2@3cb5cdba
    println(anAbstractInstance.implemented) // 21
    anAbstractSuganrInstance.f(anAbstractSuganrInstance.implemented)  // 21

    // val x:AnAbstractType = (a: Int) => println(s"sweet ${implemented}")



    // 3.  :: and #:: methods are special

    val prependedList = 2 :: List(3,4)
    // 2.::(List(3,4)) there is no :: method on Int, but still it compiles this prepend operation cuz
    // compiler rewrites this as List(3,4).::(2) , reason is
    // scala specification: last char decides associative of method
    // if it ends with ":"( 2.::(List(3,4)) here : is last last char in method) then the compiler change it to
    // right associative method like this List(3,4).::(2)

    val lst = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
    // (((List().::(5)).::(4)).::(3).::(2)).::(1) it will do prepend operation
    println(lst)


    class MyStream[T] {

      def -->:(value: T): MyStream[T] = this // actual implementation

    }

    val mystream = 1  -->: 2 -->: 3 -->: 4 -->: new MyStream[Int]

    // here at method end ":" is there that's why on Int we can call the function









  }

}
