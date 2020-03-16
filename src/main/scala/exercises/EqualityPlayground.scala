package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  /**
    * Equality
    */

  trait Equal[T] {
    def apply(x: T, y: T): Boolean
  }

  implicit object NamedEquality extends Equal[User] {
    override def apply(x: User, y: User): Boolean = x.name == y.name
  }

  object FullEquality extends Equal[User] {
    override def apply(x: User, y: User): Boolean = x.name == y.name && x.email == y.email
  }

  /*
  Exercise:

  implement type class pattern for the Equal
 */


  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer(a,b)
  }

  val sai = User("saikrishna", 25, "sai@gmail.com")
  val saiNew = User("kenchesai", 24, "ksai@gmail.com")
  println(Equal[User](sai, saiNew))  // false
  println(Equal(sai, saiNew))  // false
  // this is called AD-HOC polymorphism

  /*
    Exercise:
    improve the Equal type class with an implicit conversion class
    this class contains 2 methods
    1. ===(antoherValue: T): Boolean
    2. !==(anotherValue: T): Boolean
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equilizer: Equal[T]) = {
      equilizer(value, anotherValue)
    }

    def !==(anotherValue: T)(implicit equilizer: Equal[T]) = {
      ! equilizer(value, anotherValue)
    }
  }

  println(sai === saiNew)  // false
  println(sai !== saiNew)  // true

  /*
    Type Safe
   */
  println(sai == 34) // false, this is scala default method  can compare any objects
  // println(sai === 34)  // sai is User 34 is Int so it will give complation error(type mismatch)


}
