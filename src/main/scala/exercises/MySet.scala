package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /**
    *  Exrcise - implement a functional Set
    * */

  def apply(ele: A): Boolean = contains(ele)
  def contains(ele: A): Boolean
  def +(ele: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]
//  def isEmpty: Boolean
// def printElements: String

//  override def toString(): String = "[" + printElements + "]"

  def map[B](f: A => B): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def foreach(f: A => Unit): Unit

  /**
    *  Exercise #2
    *  1. removing an element
    *  2. intersection with another set
    *  3. difference with another set
   */

  def -(ele: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]  // difference
  def &(anotherSet: MySet[A]): MySet[A]   // intersection



   // Exercise #3 - implement a unary_! - NEGATION of a set
  def unary_! : MySet[A]

}

class EmptySet[A] extends MySet[A] {
// def isEmpty: Boolean = true
  def contains(ele: A): Boolean = false
  def +(ele: A): MySet[A] = new NonEmptySet(ele, this)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
// def printElements: String = ""

  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  def filter(predicate: A => Boolean): MySet[A] = this
  def foreach(f: A => Unit): Unit = ()

  // exercise #2
  def -(ele: A): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A]  = this


  def unary_! : MySet[A] = new PropertyBasedSet[A](x => true)

}

// all elements of type A which satisfies property(condition)
// {x in A | property(x)}
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  override def contains(ele: A): Boolean = property(ele)

  // {x in A | property(x)} + ele = {x in A | property(x) || x == ele }
  override def +(ele: A): MySet[A] = {
    if (property(ele)) this
    else new PropertyBasedSet[A](x => property(x) || x == ele)
  }

  // {x in A | property(x)} ++ otherSet = {x in A | property(x) || otherSet contains x}
  override def ++(anotherSet: MySet[A]): MySet[A] = {
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))  // anotherSet.contains(x)
  }

  // override def isEmpty: Boolean = false

  // override def printElements: String = ???

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = {  // property based
    new PropertyBasedSet[A](x => property(x) && predicate(x))
  }

  override def foreach(f: A => Unit): Unit = politelyFail

  override def -(ele: A): MySet[A] = filter(x => x != ele)

  override def --(anotherSet: MySet[A]): MySet[A] =  filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")


}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
//  def isEmpty: Boolean = false

/*
 def printElements: String = {
    if (!tail.isEmpty) s"$head ${tail.printElements}"
    else s"$head"
  }
*/

  def contains(ele: A): Boolean = {
    head == ele || tail.contains(ele)
  }
  def +(ele: A): MySet[A] = {
    if (this contains ele) this
    else new NonEmptySet(ele, this)
  }

  /* Set doesn't contain proper order
     [1 2 3] ++ [4 5]
       [2 3] ++ ([4 5] + 1)
       [2 3] ++ ([4 5] + 1 + 2)
        [] ++ ([4 5] + 1 + 2 + 3)  // calls EmptySet ++ method
          [4 5] + 1 + 2 + 3
           [4 5 1 2 3]

   */
  def ++(anotherSet: MySet[A]): MySet[A] = {
    tail ++ (anotherSet + head)
  }

  def map[B](f: A => B): MySet[B] = {
    (tail map f) + f(head)

    // another way
    // new NonEmptySet[B](f(head), tail.map(f))
  }

  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  def flatMap[B](f: A => MySet[B]): MySet[B] = {
    (tail flatMap f) ++ f(head)
  }
  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  // exercise #2
  def -(ele: A): MySet[A] = {
    if (head == ele) tail
    //else new NonEmptySet(head, tail - ele)
    else tail - ele + head
  }

  def --(anotherSet: MySet[A]): MySet[A] = {
    // filter(x => !anotherSet.contains(x))
     filter(x => !anotherSet(x))
    //filter(!anotherSet)
  }

  def &(anotherSet: MySet[A]): MySet[A] = {
    // filter(x => anotherSet.contains(x))
    // filter(x => anotherSet(x))
    filter(anotherSet)
  }

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}


object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
    buildSet(values.toSeq, new EmptySet[A])
  }
}


object MySetPlayground extends App {
  val s = MySet(1,2,3,4)
  println(s)

  println("INITIAL SET")
  println("------------")
  s foreach println
  println()

  println("ADDING AN ELEMENT")
  println("-----------------")
  s + 5 foreach println
  println()

  println("ADDING ANOTHER SET")
  println("------------------")
  s + 5 ++ MySet(-1,-2) foreach println
  println()

  println("ADDING DUPLICATE ELEMENT")  // it won't add if ele exists
  println("------------------------")
  s + 5 ++ MySet(-1,-2) + 3 foreach println
  println()

  println("MAP")
  println("---")
  s + 5 ++ MySet(-1,-2) + 3 map(x => x * 10) foreach println
  println()

  println("FLATMAP")
  println("-------")
  s + 5 ++ MySet(-1,-2) + 3 flatMap(x => MySet(x, x * 10)) foreach println
  println()

  println("FILTER")
  println("------")
  s + 5 ++ MySet(-1,-2) + 3 flatMap(x => MySet(x, x * 10)) filter(x => x % 2 == 0) foreach println

  val sNew = s + 5 ++ MySet(-1,-2) + 3 flatMap(x => MySet(x, x * 10)) filter(x => x % 2 == 0)
  println(sNew)  // [2 20 4 40 -2 -20 -10 50 30 10]

  println(sNew - (-20))  // [2 20 4 40 -2 -10 50 30 10]

  val sNew2 = sNew - (-20) - 10 + 15 + 25

  println(sNew)   // [2 20 4 40 -2 -20 -10 50 30 10]
  println(sNew2)  // [25 15 2 20 4 40 -2 -10 50 30]

  println(sNew -- sNew2)  // [-20 10]  // diff

  println(sNew & sNew2)  // [2 20 4 40 -2 -10 50 30]  // intersection


  val negative = !s  // s.unary_! = all the naturals not equal to 1,2,3,4
  println(negative(2)) // false
  println(negative(5)) // true
  println(negative(183920)) // true


  val negativeEven = negative.filter(x => x % 2 == 0)
  println(negativeEven(5)) // false
  println(negativeEven(28392831)) // false
  println(negativeEven(10203920))  // true

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5)) // true

  val negativeOdd = negative.filter(x => x % 2 == 1)
  println(negativeOdd(5)) // true
  println(negativeOdd(10)) // false

  val negatvieAll = negativeOdd ++ negativeEven
  println(negatvieAll(5))  // true
  println(negatvieAll(6))  // true

  val z = MySet(1,2,3,4)
  val total = negative ++ z
  println(total(2)) // true
  println(total(6)) // true

}



/*

   def -(elem: A): MySet[A] = {
    //for construct runs through a map, so the yield will return what a map returns
    for {
        x <- this if x != elem
    } yield x
   }

   def --(anotherSet: MySet[A]): MySet[A] = {
    //remove all elements from this, which are in anotherSet
    for {
        x <- this if !(anotherSet contains x)
    } yield x
   }

   def &(anotherSet: MySet[A]): MySet[A] = {
    //returns only the elements that are common to both sets
    for {
        x <- this if anotherSet contains x
    } yield x
   }
 */



