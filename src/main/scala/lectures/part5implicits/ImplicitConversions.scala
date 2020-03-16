package lectures.part5implicits

object ImplicitConversions extends App {

  case class Complex(real: Double, imaginary: Double = 0.0) {
    override def toString = s"$real $sign ${imaginary.abs}i"
    private def sign = if (imaginary > 0.0) "+" else "-"
    def +(other: Complex) = {
      Complex(real + other.real, imaginary + other.imaginary)
    }
  }

  val c1 = Complex(5, -6)
  val c2 = Complex(-3, 4)

  println(c1 + c2)  // 2.0 - 2.0i

  // what about

  // c1 + 6 // found Int required Complex


  // Overloading:

  case class Complex2(real: Double, imaginary: Double = 0.0) {
    override def toString = s"$real $sign ${imaginary.abs}i"
    private def sign = if (imaginary >= 0.0) "+" else "-"
    def +(other: Complex2) = Complex2(real + other.real, imaginary + other.imaginary)
    def +(other: Int) = Complex2(real + other, imaginary)
  }

  val c3 = Complex2(5, -6)
  val c4 = Complex2(-3, 4)

  println(c3 + c4)  // 2.0 - 2.0i
  println(c3 + 6)  // 11.0 - 6.0i
  println(Complex2(5, 6)  + 10)  // 15.0 + 6.0i


  //10 + c3
  // Implicit Conversions:

  case class ComplexNew(real: Double, imaginary: Double = 0.0) {
    override def toString = s"$real $sign ${imaginary.abs}i"
    private def sign = if (imaginary > 0.0) "+" else "-"
    def +(other: ComplexNew) = {
      ComplexNew(real + other.real, imaginary + other.imaginary)
    }
  }
  object ComplexNew {
    implicit def intToComplexNew(x: Int): ComplexNew = ComplexNew(x)
  }

  // Note: no longer need the overloaded + either

  //When Scala has a type problem between two types, it looks for an implicit
  //conversions to/from either type that will solve it

  val cc1 = ComplexNew(5, -6)
  val cc2 = ComplexNew(-3, 4)

  println(cc1 + cc2) // 2.0 - 2.0i
  println(cc1 + 5)  // 10.0 - 6.0i
  println(10 + cc2)  // 7.0 + 4.0i



  // extension methods

  class TimesInt(i: Int) {
    def times(fn: => Unit): Unit = {
      var x = 0
      while (x < i) {
        x += 1
        fn
      }
    }
  }

  implicit def intToTimesInt(i: Int): TimesInt = new TimesInt(i)


  5 times { println("hello")}

  //         (OR)

  intToTimesInt(5).times {println("hello")}


  // we can make class as implicit so we can avoid writing intToTimesInt method separately


  implicit class TimesIntNew(i: Int) {
    def timesNew(fn: => Unit): Unit = {
      var x = 0
      while (x < i) {
        x += 1
        fn
      }
    }
  }

  println(5 timesNew{ println("world") })






}
