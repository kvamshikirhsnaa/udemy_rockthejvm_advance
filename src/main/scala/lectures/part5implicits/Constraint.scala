package lectures.part5implicits

object Constraint extends App {


  trait Food {
    def name: String
  }

  case class Fruit(name: String) extends Food
  case class Cereal(name: String) extends Food
  case class Meat(name: String) extends Food

  trait Eater {
    def name: String
  }

  case class Vegan(name: String) extends Eater
  case class Vegetarian(name: String) extends Eater
  case class Paleo(name: String) extends Eater

  val apple = Fruit("Apple")
  val alpen = Cereal("Alpen")
  val chicken = Meat("Chicken")

  val alice = Vegan("Alice")
  val bob = Vegetarian("Bob")
  val charlie = Paleo("Charlie")


  //import org.scalactic.Explicitly

  import scala.annotation.implicitNotFound

  //@implicitNotFound(msg = s"Illegal feeding: No eat rules from ${EATER} to ${FOOD}")
  trait Eats[EATER <: Eater, FOOD <: Food] {
    def feed(food: FOOD, eater: EATER): String = s"${eater.name} eats ${food.name}"
  }

  // Let's write a restrictive feedTo method:

  def feedTo[FOOD <: Food, EATER <: Eater](food: FOOD, eater: EATER)(implicit ev: Eats[EATER,FOOD]) = {
    ev.feed(food, eater)
  }

  //feedTo(apple, alice)

  object VeganRules {
    implicit object VeganEatsFruit extends Eats[Vegan, Fruit]
  }

  import VeganRules._

  feedTo(apple, alice)

  //feedTo(apple, bob)

  object VegetarianRules {
    implicit object vegEatsFruit extends (Vegetarian Eats Fruit)
    implicit object vegEatsCereal extends (Vegetarian Eats Cereal)
  }

  import VegetarianRules._

  feedTo(apple, alice)

  feedTo(apple, bob)

  //feedTo(chicken, charlie) // no implicits found for Eats[Paleo, Meat]

  //feedTo(chicken, alice) // no implicits found for Eats[Vegan, Meat]

  object PaleoRules {
    implicit val paleoEatsFruit = new (Paleo Eats Fruit) {}
    implicit val paleoEatsMeat = new (Paleo Eats Meat) {}
  }

  import PaleoRules._

  feedTo(chicken, charlie)

  // Both work, but overall I think object extends looks cleaner than val
  // if it need to take parameters using def is better it will take parameters


  object AllRules {
    implicit def paleoEatsFruit: Eats[Vegan,Fruit] = new (Vegan Eats Fruit) {}
    implicit def paleoEatsMeat: Eats[Vegan,Meat] = new (Vegan Eats Meat) {}
  }

  import AllRules._

  // feedTo(chicken, alice)


  // Explicitly writing

  def feedTo2(food: Food, eater: Eater)(implicit ev: Eats[Eater,Food]) = {
    ev.feed(food, eater)
  }

  feedTo2(chicken, alice)(new (Eater Eats Food){})

  feedTo2(alpen, alice)(new (Eater Eats Food){})

}
