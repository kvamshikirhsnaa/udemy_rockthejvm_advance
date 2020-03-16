package lectures.part4implicits

object OrganizingImplicits extends App {

   println(List(1,5,2,6,3,4).sorted)  // List(1, 2, 3, 4, 5, 6)

  // sorted method takes one implicit arg Ordering but this will be provided by Scala.Predef library

  implicit val reverseOrdering: Ordering[Int]= Ordering.fromLessThan(_ > _)
  // implicit def reverseOrdering: Ordering[Int]= Ordering.fromLessThan(_ > _) // valid
  // implicit def reverseOrdering(): Ordering[Int]= Ordering.fromLessThan(_ > _) // invalid, shouldn't have parentheses
  println(List(1,5,2,6,3,4).sorted)  // List(6, 5, 4, 3, 2, 1)

/*
  implicit  val normalOredering: Ordering[Int] = Ordering.fromLessThan(_ < _)
  println(List(1,5,2,6,3,4).sorted)

  throws ambiguous error, 2 implicits are found for Ordering[Int] so it will throw error
*/

  /*
     Implicits (used as implicit parameters):
       - val/var
       - object
       - accessor methods - defs with no parentheses
  */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Saikrishna", 25),
    Person("Prakash", 27),
    Person("AravindSwamy", 25)
  )

  object Person {
    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((x,y) => x.age < y.age )
  }

  implicit val alphabateOrdering: Ordering[Person] = Ordering.fromLessThan((x,y) => x.name.compareTo(y.name) < 0 )

  println(persons.sorted)  // List(Person(AravindSwamy,25), Person(Prakash,27), Person(Saikrishna,25))
  // sort based on age , cuz there are 2 implicits one is for ageOrdering and alphabateOrdering but
  // alphabateOrdering is defined in companion object ageOrdering defined in local scope so
  // first it will give priority to local scope implicit value

  /*
      val a = "saikrishna"

      val b = "aravind"

      a compareTo b
      res5: Int = 18

      b compareTo a
      res6: Int = -18

      (b compareTo a) < 0
      res8: Boolean = true
   */


  /*
     Implicit Scope:
     - normal scope: LOCAL SCOPE
     - imported scope:
     - companions of all types involved in the method signature
        - List
        - Ordering
        - all the types involved = A or Any supertype
   */

  // def sorted[B >: A](implicit ord: Ordering[B]): List[B]


  /*
     Exercise:

     - totalPrice = most used (50%)
     - by unit count = 25%
     - by unit price = 25%

   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {

    implicit val totalPriceOrdering: Ordering[Purchase] = {
      Ordering.fromLessThan((x,y) => x.nUnits * x.unitPrice < y.nUnits * y.unitPrice)
    }
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = {
      Ordering.fromLessThan((x, y) => x.nUnits < y.nUnits)
    }
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = {
      Ordering.fromLessThan((x, y) => x.unitPrice < y.unitPrice)
    }
  }












}
