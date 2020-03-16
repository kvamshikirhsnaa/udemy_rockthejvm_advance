package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1  // Function1[Int,Int] == Int => Int

  val aFussyFunction = (x: Int) => {
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableFunction
  }
  println(aFussyFunction(2))  // 56

  class FunctionNotApplicableFunction extends RuntimeException

  val aNiceFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // normal function
  println(aNiceFussyFunction(5))  // 999
  //println(aNiceFussyFunction(10))  // throws scala.MatchError

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial functions
  println(aPartialFunction(1))
 // println(aPartialFunction(10)) //  throws scala.MatchError


  // PF utilities

  //isDefinedAt(x: T)
  // it takes one argument type is PartialFunction input type, returns Boolean
  println(aPartialFunction.isDefinedAt(5))  // true
  println(aPartialFunction.isDefinedAt(4))  // false

  // lift(x: T)
  // it takes one argument type is PartialFunction input type, returns Option[T]
  val lifted = aPartialFunction.lift(2)
  val liftedNew = aPartialFunction.lift(3)
  println(lifted)  // Some(56)
  println(liftedNew)  // None


  // chaining partial functions
  // orElse takes parameter type return new partial function,
  // here aPartialFunction and orElse both should have same parameter type, which is [Int, Int] in this case
  val pfchain = aPartialFunction.orElse[Int, Int] {
    case 10 => 50
  }
  println(pfchain(2)) // 56
  println(pfchain(10)) // 50

/*
  val pfchain = aPartialFunction.orElse[Int, String] {
    case 10 => "hello i am ten"
  }
  like this should not declare, partial function types and orElse types should match
*/

  // PF extends normal Functions
  val aTotalFunction: Int => String = {
    case 1 => "i am One"
  }

  println(aTotalFunction(1))  // i am One
  // println(aTotalFunction(3))  // throws scala.MatchError

  // HOFs accepts Partial Functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 99
  }
  println(aMappedList)  // List(42, 78, 99)


  val aMappedList2 = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 =>
  }
  println(aMappedList2)  // List(42, 78, ())


/*
  val aMappedList3 = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 4 => 20
  }
  println(aMappedList3)  // throws scala.MatchError
  */


  /*
     PF can only have ONE parameter type
   */

  /**
    *  Exercise
    *
    * 1. construct a PF instance yourself (anonymous class)
    * 2. dumb chatbot as a PF
    *
   */

  // 1. construct a PF instance yourself (anonymous class)
  val manualFussyFunction: PartialFunction[Int, Int] = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 | 2 | 5 => x * 2
    }

    override def isDefinedAt(x: Int): Boolean = {
      x == 1 || x == 2 || x == 5
    }
  }

  println(manualFussyFunction(5))  // 10
  // println(manualFussyFunction(50))  // throws scala.MatchError
  println(manualFussyFunction.isDefinedAt(2))  // true
  println(manualFussyFunction.isDefinedAt(3))  // false

  // 2. dumb chatbot as a PF

  val chatbotPf: PartialFunction[String, String] = {
    case "hi, how are you" => "hey, i am good"
    case "thinnava ra" => "ha thinna ra"
    case "em chestunnav" => "em le, kali"
  }

  // scala.io.Source.stdin.getLines().foreach(input => println(chatbotPf(input)))

  // can also write like this keeping input msgs in map and returning result
  scala.io.Source.stdin.getLines().map(chatbotPf).foreach(println)













}
