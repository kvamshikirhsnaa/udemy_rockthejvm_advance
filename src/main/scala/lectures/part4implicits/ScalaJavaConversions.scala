package lectures.part4implicits

import java.{util => ju}
import collection.mutable._
import collection.JavaConverters._

object ScalaJavaConversions extends App {

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)  // [1, 2, 3, 4, 5]

  val scalaSet = javaSet.asScala

  /*
     Iterator
     Iterable
     ju.List -> collection.mutable.Buffer
     ju.Set -> collection.mutable.Set
     ju.Map -> collection.mutable.Map
   */

  val numbersBuffer = ArrayBuffer[Int](1,2,3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer)  // true

  // some scala to java conversion can't give same object
  // scala list is immutable java list is mutable
  val numbersList = List(1,2,3)
  val juNumbers = numbersList.asJava
  println(numbersList)  // List(1, 2, 3)
  println(juNumbers) // [1, 2, 3]

  // converting back to Scala from java
  val numbersListNew = juNumbers.asScala
  // from java list converted to scala Buffer as java list and scala buffer both are mutable
  println(numbersListNew)  // Buffer(1, 2, 3)

  println(numbersList eq juNumbers)       // false
  println(numbersList eq numbersListNew)  // false
  println(numbersList == numbersListNew)  // true

  /*
    == : Equals (the == method) is the "deep" equality which tests whether two objects are "the same".
    eq : The eq method is the reference equality, in that you test whether two values points to the
    exact same object in memory.
   */

  // juNumbers.add(7) // java.lang.UnsupportedOperationException
  // java list is mutable but scala list is immutable, we converted scala immutable list to java list
  // so it doesn't behave like mutable list


  /*
     exercise:
     create a Scala-Java Optional-Option
       .asScala method
   */

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = {
    new ToScala[Option[T]](
      if (o.isPresent) Some(o.get)
      else None
    )
  }

  /*

  trait ToScala[T] {
    def asScala: T
  }

  implicit class JavaToScalaOption[T](o: java.util.Optional[T]) extends ToScala[Option[T]] {
    override def asScala: Option[T] = if (o.isPresent) Some(o.get) else None
  }

   Note:  Any implicit class is actually equivalent to a regular class/trait + an implicit def.
   */

  val juOptional: ju.Optional[Int] = ju.Optional.of(3 )
  val scalaOption = juOptional.asScala

  println(juOptional)  // Optional[3]
  println(scalaOption)  // Some(30)




}
