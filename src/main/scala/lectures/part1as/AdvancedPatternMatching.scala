package lectures.part1as

object AdvancedPatternMatching {
  def main(args: Array[String]): Unit = {

    val numbers = List(1)
    val description = numbers match {
      case head :: Nil => println(s"the only element is $head.")
      case _ =>
    }
    description  // the only element is 1.

    /*
       - Constants
       - wildcards
       - case classes
       - tuples
       - some special magic like above
        99% we can use this pattern matching, but 1% if we need to write own pattern matching then follow this

     */

    // class members can't decompose using pattern matching like case classes, so we need to implement our own
    // pattern matching if we want to decompose class members
    class Person(val name: String, val age: Int)

    // to implement own pattern matching for classes we should create companion object
    // in side object create unapply() method

    object Person {
      def unapply(person: Person): Option[(String, Int)] = {
        if (person.age < 18) Some((person.name, person.age))
        else None
      }

      // we can create multiple unapply methods(overload)
      def unapply(age: Int): Option[String] = {
        Some(if (age < 21) "minor" else "major")
      }
    }

    val p1 = new Person("Nani", 13)
    //val p2 = new Person("Saikrishna", 25)

    val greeting = p1 match {
      case Person(n,a) => s"hi, i am $n, i am $a years old"
    }
    // p2 returns None so pattern match will throw scala.MatchError cuz we didn't written case for none match
    println(greeting)  // hi, i am Nani, i am 13 years old

    val p3 = new Person("Nani", 13)
    val legalStatus = p3.age match {
      case Person(status) => s"my legal status is $status"
    }
    println(legalStatus)  // my legal status is minor

    /*
       Exercise
     */

    val n: Int = 18
    val mathProperty = n match {
      case x if x < 10 => "single digit"
      case x if x % 2 == 0 => "an even number"
      case _ => "no property"
    }
    println(mathProperty)  // an even number

    // instead of writing so many case conditions implement own pattern matching using unapply method for above

    // here object name should starts with small letter
    object even {
      def unapply(x: Int): Option[Boolean] =
        if (x % 2 == 0) Some(true)
        else None
    }

    object singleDigit {
      def unapply(x: Int): Option[Boolean] =
        if (x > -10 && x < 10) Some(true)
        else None
    }

    val k = 99
    val mathProperty2 = k match {
      case singleDigit(_) => "single digit"
      case even(_) => "an even number"
      case _ => "no property"
    }
    // here in case condition object name we have to use (_) for single argument
    // if we remove(_) always first condition will return
    println(mathProperty2)  // no property


    // also we can use unapply return type without Option type only Boolean
    object odd {
      def unapply(x: Int): Boolean = x %2 != 0
    }

    object singleNDoubledigit {
      def unapply(x: Int): Boolean = x > -99 && x < 99
    }

    val z = 9909

    val mathProperty3 = z match {
      case singleNDoubledigit() => "single or double digit"
      case odd() => "an odd number"
      case _ => "no property"
    }
    println(mathProperty3)  // an odd number
    // (_) -> if return type is Option[Boolean], we can put any var also inside
    // ()  -> if return type is Boolean only with out Option


    /*
      for any class if we want to implement decompose members of class we should create companion object
      and implement unapply method
      we can develop many unapply()(method overloading) methods inside object

      if we want to avoid writing long case conditions implement unapply method by in side an object
      but object name should start with small character not capital character
      in this we can write condition using unapply method, return type can be Option[Boolean] or Boolean
      if we give Option[Boolean] in case condition object(_) use "_" symbol inside parenthesis
      if return type was Boolean in case condition object() should n't use "_" symbol but parenthesis are mandatory

     */


    val g = (1, "Ganga Bhavani Reddy ")

    object isganga {
      def unapply(tuple: (Int, String)): Option[Boolean] = {
        if (tuple._2 == "Ganga Bhavani Reddy ") Some(true)
        else None
      }
    }

    val gMatcher = g match {
      case isganga(_) => s"yes, she is ganga"
    }

    println(gMatcher) // yes, she is ganga


  }

}
