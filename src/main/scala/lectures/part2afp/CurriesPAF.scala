package lectures.part2afp

object CurriesPAF extends App {

  // curried function
  val superAdder: Int => Int => Int = {
    x => y => x + y
  }
  // superAdder is curried function
  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5)) // 8
  println(superAdder(3)(5)) // 8

  def curriedAdder(x: Int)(y: Int): Int = x + y  // curried method

  val add4:Int => Int = curriedAdder(4) // Int => Int
  println(add4(5)) // 9
  println(curriedAdder(5)(4))

/*
  val add5 = curriedAdder(5)
  this will not compile if the return type is not specified for curried function
  Error:(20, 26) missing argument list for method curriedAdder in object CurriesPAF
  Unapplied methods are only converted to functions when a function type is expected.
  You can make this conversion explicit by writing `curriedAdder _` or `curriedAdder(_)(_)` instead of `curriedAdder`.
  val add5 = curriedAdder(5)

*/

  val add5 = curriedAdder(5)_
  println(add5(6)) // 11


  // Exercise
  val simpleAddFunction = (x:Int, y:Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many different implementation of add7 using the above
  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add8 = (x: Int) => simpleAddMethod(7, x)

  println(add7(2)) // 9
  println(add8(2)) // 9

  val add7_2 = simpleAddFunction.curried(7)
  println(add7_2)  // scala.Function2$$Lambda$19/1854778591@7a79be86
  println(add7_2(2))  // 9

  val add7_3 = simpleAddFunction(7, _: Int)
  println(add7_3)  // lectures.par2afp.CurriesPAF$$$Lambda$20/885951223@b684286
  println(add7_3(2)) // 9

  val add7_4 = curriedAddMethod(7) _ // PAF
  val add7_5 = curriedAddMethod(7)(_) // PAF = alternative syntax
  println(add7_4)  // lectures.par2afp.CurriesPAF$$$Lambda$20/191382150@3f3afe78
  println(add7_5(2)) // 9

  val add7_6 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
               // y => simpleAddMethod(7,y)
  println(add7_6) // lectures.par2afp.CurriesPAF$$$Lambda$22/2137211482@36d64342
  println(add7_6(2))  // 9

  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c
  val greet = concatenator("Hello, I'm ", _: String, ", How are you?")
  println(greet("Saikrishna")) // Hello, I'm Saikrishna, How are you
  println(greet("Aravind"))  // Hello, I'm Aravind, How are you

  val greetNew = concatenator(_: String, _: String, ", How are you?")
  println(greetNew("Hello, I'm ", "Vamshikrishna")) // Hello, I'm Vamshikrishna, How are you?

  /*
      Exercise: 1
     1. Process a list of numbers and return their string representation with different formats
        use %4.2f, %8.6f and %14.12f with a curried formatter function


*/

  println("%4.2f".format(Math.PI))  // 3.14
  println("%8.6f".format(Math.PI))  // 3.141593

  def curriedFormatter(s: String)(number: Double): String = s.format(number)

  println(curriedFormatter("%4.2f")(Math.PI)) // 3.14

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val simpleFormat = curriedFormatter("%4.2f") _  // lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat)) // List(3.14, 2.72, 1.00, 9.80, 0.00)
  println(numbers.map(seriousFormat)) // List(3.141593, 2.718282, 1.000000, 9.800000, 0.000000)
  println(numbers.map(preciseFormat))// List(3.141592653590, 2.718281828459, 1.000000000000, 9.800000000000, 0.000000000001)

/*
       Exercise: 2
      2. difference b/w
        - functions vs methods
        - parameters: by-name vs 0-lambda
 */

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
     calling byName and byFunction
     - int
     - method
     - parenMethod
     - lambda
     - PAF
   */

  byName(2) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware  ==> byName(parenMethod())
  /*
     def sample() = 2
     sample() => o/p: 2
     sample  => o/p: 2
   */

  // byName(() => 42) // not ok
  byName((() => 42)()) // ok
  // byName(parenMethod _) // not ok

  // byFunction(45) // not ok
  // byFunction(method) // not ok
  byFunction(method _ )  // ok
  byFunction(parenMethod) // ok
  // byFunction(parenMethod()) // not ok
  byFunction(() => 46) // ok
  byFunction(parenMethod _) // also works, but warning unnecessary











}
