package lectures.part5implicits

object TypeClass3 extends App {

  trait Reversable[T] {
    def reverse(x: T): T
  }

  def reverseNow[T](x: T)(implicit rev: Reversable[T]): T = {
    rev.reverse(x)
  }

  implicit object ReversableString extends Reversable[String] {
    override def reverse(x: String) = x.reverse
  }

  implicit object ReversableInt extends Reversable[Int] {
    override def reverse(x: Int) = x.toString.reverse.toInt
  }

  println(reverseNow(1000))  // 1
  println(reverseNow(1234))  // 4321
  println(reverseNow("hello"))  // olleh

  implicit def rverse[T: Reversable](value: T): T = {
    val rev = implicitly[Reversable[T]]
    rev.reverse(value)
  }

  println(rverse(1234)) // 4321
  println(rverse("hello")) // olleh

/*
  implicit def reversableList[T: Reversable]: Reversable[List[T]] = new Reversable[List[T]] {
    override def reverse(xs: List[T]) = xs.map(x => rverse(x)).reverse
  }
*/
  //          OR
  // using single abstract method
  implicit def reversableList[T: Reversable]: Reversable[List[T]] =  {
    (xs: List[T]) => xs.map(x => rverse(x)).reverse  // .reverse is Scala reverse on List
  }



  implicit class ReversingAll[T](value: T)(implicit rev: Reversable[T]) {
    def reversing: T = rev.reverse(value)
  }

  println("hello".reversing)   // olleh
  println(List(123, 456, 100).reversing)    // (List(1, 654, 321))
  println(List("hello", "world").reversing)  // List(dlrow, olleh)


  implicit class ReverseAll[T: Reversable](value: T) {
    val rev = implicitly[Reversable[T]]
    def reverseIt: T = rev.reverse(value)
  }

  println(1234.reverseIt)  // 4321
  println("hello".reverseIt)  // olleh

  println(List(123, 456, 789).reverseIt) // (List(987, 654, 321))
  println(List("Hello", "Old", "Bean").reverseIt) // List(naeB, dlO, olleH)

  println(rverse(List(123, 456, 789))) // List(987, 654, 321)
  println(rverse(List("Hello", "Old", "Bean"))) // List(naeB, dlO, olleH)





}
