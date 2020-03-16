package lectures.part2afp

object LazyEvaluation extends App {

  // it crash the program
  // val x: Int = throw new RuntimeException

  // if lazy key word used before it won't crash,
  // when the lazy variable is called then only it will compute
  lazy val x: Int = throw new RuntimeException

  //it will crash the program
  // println(x)

  lazy val y = {
    println("hello")
    3
  }
  println(y)
  // output of above is: hello
  //                     3
  println(y)
  // if lazy value once computed it won't compute again
  // output of above is: 3

  // examples of implications
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no")
  // output: no


  // in conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n + 1
  def retrieveMagicValue = {
    // side effect or long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod(retrieveMagicValue))
  // output: waiting
  //         waiting
  //         waiting
  //         127
  //here byNameMethod taking one argument 'n' which is an anonymous function(that doesn't take any argument returns an int)
  // when this argument 'n' is used it will computed every time when it's called in implementation
  // if the 'n' never called in implementation it won't compute it's value
  // here 'n' is called 3 times so 3sec it's waiting and printing waiting 3 times before actual result


  // use lazy vals
  // CALL BY NEED
  def byNameMethodNew(n: => Int): Int = {
    lazy val t = n  // only evaluated once
    t + t + t + 1
  }
  println(byNameMethodNew(retrieveMagicValue))
  // output:  waiting
  //          127
  // this time instead of 'n' we assinged 'n' value to a lazy val t
  // first time it will wait once t val computed it won't compute again like 'n'
  // so we are seeing waiting only once


  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)

  val lt30 = numbers.filter(lessThan30)
  println(lt30)

  val gt20 = lt30.filter(greaterThan20)
  println(gt20)


  // withFilter: withFilter is a function on collection, returns lazy vals under hood

  val lt30lazy = numbers.withFilter(lessThan30)

  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println
  println(gt20lazy)

  gt20lazy.foreach(println)


  // for-comprehensions use withFilter with guards
  for {
    a <- List(1,2,3,4) if a % 2 == 0  // use lazy vals!
  } yield a + 1

  List(1,2,3,4).withFilter(x =>  x % 2 == 0).map(x => x + 1) // List[Int]


  /*
     Exercise: implement a lazily evaluated, singly linked STREAM of elements

     naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers(potentially infinite)

     naturals.take(100) // lazily evaluated stream of the first 100 naturals
     naturals.take(100).foreach(println) // print first 100 naturals
     naturals.foreach(println)  // it will crash bcuz it's infinite
     naturals.map(_ * 2) // stream of all even numbers(potentailly infinite)

   */













}
