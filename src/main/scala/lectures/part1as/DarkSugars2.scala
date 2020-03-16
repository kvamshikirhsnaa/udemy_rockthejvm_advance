package lectures.part1as

object DarkSugars2 {
  def main(args: Array[String]): Unit = {


    // syntax sugar

    // 4. multi word method naming
    // if method has multiple words with space like that we can define usin `<method>`
    class TeenGirl(name: String) {
      def `and then said`(gossip: String) = println(s"$name said, $gossip")
    }

    val tg = new TeenGirl("priya")
    tg.`and then said`("Scala is so sweet")
    tg `and then said` "Scala is so sweet"   // can also write like this

    // val a string = "Hello, Scala"
    val `a string` = "Hello, Scala"
    println(`a string`)

    // 5. infix types
    class Composite[A,B]
    // val composite: Composite[Int, String] = ???
    // val compositeNew: Int Composite String = ???  // infix types, works when only takes 2 type parameters

    class -->[A,B]
    // val towards: Int --> String = ???


    // 6: update() method is very special, much like apply
    // update() is very usable in mutable collection
    val arr = Array(1,2,3,4)
    println(arr.toList)  // List(1, 2, 3, 4)
    arr(3) = 6  // arr.update(3,6) compiler calls like this
    println(arr.toList)  // List(1, 2, 3, 6)


    // 7. setters for mutable containers
    // for setters and getters name should be same if not won't work
    class Mutable {
      private var internalMember: Int = 0
      def member = internalMember  // getter
      def member_=(value:Int): Unit = {  // setter
        internalMember = value
      }
    }

    val aMutableContainer = new Mutable
    println(aMutableContainer.member)  // 0
    aMutableContainer.member = 6
    println(aMutableContainer.member)  // 6












  }
}
