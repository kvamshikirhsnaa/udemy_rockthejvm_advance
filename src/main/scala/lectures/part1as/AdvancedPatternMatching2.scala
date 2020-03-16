package lectures.part1as

object AdvancedPatternMatching2 {
  def main(args: Array[String]): Unit = {

    // infix patterns
    case class Or[A,B](a: A, b: B)
    val either = Or(3, "three")
    val humanDescription = either match {
      //case Or(num, str) => s"$num is written as $str"
      case num Or str => s"$num is written as $str" // can write like this also
    }
    println(humanDescription)  // 3 is written as three

    // decomposing sequences
    val numbers = List(1)
    val vararg = numbers match {
      case List(1, _*) => "starting with 1"
    }
    println(vararg)  // starting with 1

    abstract class MyList[+A] {
      def head: A = ???
      def tail: MyList[A] = ???
    }

    case object Empty extends MyList[Nothing]{
      override def head: Nothing = throw new NoSuchElementException
      override def tail: MyList[Nothing] = throw new NoSuchElementException
    }

    case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

    // implementing _* pattern matching for out own MyList
    object MyList {
      def unapplySeq[A](list: MyList[A]): Option[Seq[A]] = {
        if (list == Empty) Some(Seq.empty)   // Seq.empty returns List()
        else unapplySeq(list.tail).map(x => list.head +: x)
      }
    }

    /*
       val lst = List(1,2,3,4)
       def sample(x: List[Int]): Option[List[Int]] = Option(x)
       println(sample(lst)) // Some(List(1,2,3,4))
       println(sample(lst.tail)  // Some(List(2,3,4))
       sample(lst.tail).foreach(x => println(x))  // List(2,3,4)
       sample(lst.tail).map(lst.head +: _) // Some(List(1,2,3,4))

     */

    val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Cons(4, Empty))))

    val decomposed = myList match {
      case MyList(1,2,_*) => "starting with 1 and 2"
      case _ => "something else"
    }
    println(decomposed)  // starting with 1 and 2



    // custom return types for unapply method
    // unapply method return type always not necessary to be an Option
    // data structure that we use as return type only needs to have two defined methods
    // they are isEmpty: Boolean  and  get: something

    abstract class Wrapper[T] {
      def isEmpty: Boolean
      def get: T
    }

    class Person(val name: String, val age: Int)

    object PersonWrapper {
      def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
        override def isEmpty: Boolean = false

        override def get: String = person.name
      }
    }

    val p1 = new Person("Saikrishna", 25)

    val nameMatcher = p1 match {
      case PersonWrapper(n) => s"this person's name is $n"
      case _ => "an Alien"
    }
    println(nameMatcher)  // this person's name is Saikrishna


    abstract class WrapperNew[T] {
      def isEmpty: Boolean
      def get: T
    }

    object PersonWrapperNew {
      def unapply(person: Person): Wrapper[(String, Int)] = new Wrapper[(String, Int)] {
        override def isEmpty: Boolean = false

        override def get: (String, Int) = (person.name, person.age)
      }
    }

    val nameMatcherNew = p1 match {
      case PersonWrapperNew(n,a) => s"this person's name is $n, he is $a years old"
      case _ => "an Alien"
    }
    println(nameMatcherNew)  // this person's name is Saikrishna


    /*
       return type for unapply is no need to be Option ,
       if these below 2 methods implemented we can call that class as return type
        1) isEmpty: Boolean
        2) get: something

        // based on get method return type we can do pattern match
        // if isEmpty is true, it won't check get method, automatically executes default case condition(if declares)

        but mostly Options are enough to write own custom pattern matching
     */




    }

}
