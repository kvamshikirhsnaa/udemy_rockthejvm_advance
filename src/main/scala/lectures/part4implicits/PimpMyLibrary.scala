package lectures.part4implicits

object PimpMyLibrary extends App {

  // 2.isEven

  implicit class RichInt(val value: Int) {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value )

    def times(f: () => Unit): Unit = {
      (1 to value).foreach(_ => f())
    }

    def *[T](lst: List[T]): List[T] = {
      def rec(n: Int, lst2: List[T]): List[T] = {
        if (n == 0) lst2
        else rec(n - 1, lst ++ lst2)
      }
      rec(value, List())
    }

  }

  println(new RichInt(36).isEven) // true
  println(36.isEven)  // true
  // this is called type enrichment or pimping


  // compiler doesn't do multiple implicit searches
  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  println(new RichInt(25).isOdd)  // true
 // println(25.isOdd) // can not identify isOdd method on Int

   /*
      Exercise:
      enrich the String class
      - asInt
      - encrypt (ganga -> icpic)

      keep enrich the Int class
      - times(function)
          3.times(fun => ...)
      - *(lst: List)
          3 * List(1,2) => List(1,2,1,2,1,2)
    */

  implicit class RichString(str: String) {
    def asInt = Integer.valueOf(str)
    def encrypt = str.map(c => (c + 2).toChar)
  }

  println("25".asInt)  // 25
  println("ganga".encrypt) // icpic

  3.times(() => println("Scala rocks"))
  println(3 * List(1,2,3)) // List(1, 2, 3, 1, 2, 3, 1, 2, 3)


  implicit def StringToInt(str: String): Int = Integer.valueOf(str)
  println("25" / 5) // 5

  //equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
    if (n) do something
    else do something else
   */

  val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue)  // Something wrong

  /*
    Note:
    1. keep type enrichment to implicit classes and type classes
    2. avoid implicit defs as much as possible, cuz very hard to trace bugs if method has
    3. package implicits clearly, bring into scope only what you need
    4. if you need conversion, make them specific

   */





}
