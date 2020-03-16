package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair = "Saikrishna" -> "Delhi"
  val intPair = 1 -> 101

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)


  println("Saikrishna".greet)  // Hi, my name is Saikrishna
  // compiler re writes under hood and compile => println(fromStringToPerson("Saikrishna").greet)
  println("Saikrishna") // Saikrishna only not Person(Saikrishna)


  // already another implicit is in scope from String to greet method

  class A {
    def greet2 = 21
  }

  implicit def fromStringToA(str: String): A = new A

  println("ffrfer".greet2)


  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 100

  println(increment(20)) // 120
  println(increment(20)(150)) // 170


}
