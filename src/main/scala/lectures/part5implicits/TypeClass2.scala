package lectures.part5implicits

object TypeClass2 extends App {

  import scala.annotation.implicitNotFound

  abstract class CompareT[T] {
    def isSmaller(l1: T, l2: T): Boolean
    def isLarger(l1: T, l2: T): Boolean
  }

  def genInsert[T](item: T, rest: List[T])(implicit cmp: CompareT[T]): List[T] = {
    rest match {
      case Nil => List(item)
      case head :: _ if cmp.isSmaller(item, head) => item :: rest
      case head :: tail => head :: genInsert(item, tail)
    }
  }

  def genSort[T](xs: List[T])(implicit cmp: CompareT[T]): List[T] = xs match{
    case Nil => Nil
    case head :: tail => genInsert(head, genSort(tail))
  }


  implicit object compareInts extends CompareT[Int] {
    override def isSmaller(l1: Int, l2: Int): Boolean = l1 < l2
    override def isLarger(l1: Int, l2: Int): Boolean = l1 > l2
  }

  val nums = List(1,4,6,9,3,5,2)

  genSort(nums) {
    new CompareT[Int] {
      override def isSmaller(l1: Int, l2: Int): Boolean = l1 < l2

      override def isLarger(l1: Int, l2: Int): Boolean = l1 > l2
    }
  }

  genSort(nums)  // here CompareT[Int] is assigned by implicit

  //implicit val compareInts = new CompareT[Int] {
  //  override def isSmaller(l1: Int, l2: Int): Boolean = l1 < l2
  //  override def isLarger(l1: Int, l2: Int): Boolean = l1 > l2
  //}
  //
  //val nums = List(1,4,6,9,3,5,2)
  //
  //genSort(nums)


  //implicit def compareInts2 = new CompareT[Int] {
  //  override def isSmaller(l1: Int, l2: Int): Boolean = l1 < l2
  //  override def isLarger(l1: Int, l2: Int): Boolean = l1 > l2
  //}
  //
  //
  //val nums2 = List(1,4,6,9,3,5,2)
  //
  //genSort(nums2)

  // we can create like val, def and object and implemnet the trait class members
  // if we use implicit def we can give parameters to if requires
  //

  case class Person(name: String, age: Int, sal: Int)

  val p1 = Person("saikrishna",24,100000)
  val p2 = Person("aravind",24,90000)
  val p3 = Person("prakash",26,70000)
  val p4 = Person("narahari",30,80000)
  val p5 = Person("tilak",6,1000)
  val p6 = Person("nani",13,20000)
  val p7 = Person("gnani",39,90000)
  val p8 = Person("govind",42,50000)


  val persons = List(p1,p2,p3,p4,p5,p6,p7,p8)
  //genSort(persons) //it throws error saying no implicits Found for Person

  implicit object CompareTPerson extends CompareT[Person] {
    override def isSmaller(l1: Person, l2: Person): Boolean = if (l1.sal - l2.sal == 0) l1.age < l2.age else l1.sal < l2.sal
    override def isLarger(l1: Person, l2: Person): Boolean = if (l2.sal - l1.sal == 0) l1.age > l2.age else l1.sal > l2.sal
  }

  genSort(persons)


  /*
    * scala> val a = List(List(1,2,3),List(4,5),List(6,7,8,9),List(10,11,12))
    * a: List[List[Int]] = List(List(1, 2, 3), List(4, 5), List(6, 7, 8, 9), List(10, 11, 12))
    *
    * // overriding compare method, return type is Int
    * scala> implicit object ListOrdering extends Ordering[List[Int]] {
    * | override def compare(a: List[Int], b: List[Int]) = a.size - b.size
    * | }
    * defined object ListOrdering
    *
    * scala> a.sorted
    * res42: List[List[Int]] = List(List(4, 5), List(1, 2, 3), List(10, 11, 12), List(6, 7, 8, 9))
    *
    *
    * //  fromLessThan: return type is Boolean
    * scala> val a = List(List(1,2,3),List(4,5),List(6,7,8,9),List(10,11,12))
    * a: List[List[Int]] = List(List(1, 2, 3), List(4, 5), List(6, 7, 8, 9), List(10, 11, 12))
    *
    * scala> implicit val lstOrdering: Ordering[List[Int]] = Ordering.fromLessThan((x, y) => x.size < y.size)
    * lstOrdering: Ordering[List[Int]] = scala.math.Ordering$$anon$9@b671dda
    *
    * scala> a.sorted
    * res1: List[List[Int]] = List(List(4, 5), List(1, 2, 3), List(10, 11, 12), List(6, 7, 8, 9))
    *
    * // Sorting in descending order
    * scala> implicit val lstOrdering: Ordering[List[Int]] = Ordering.fromLessThan((x, y) => x.size > y.size)
    * lstOrdering: Ordering[List[Int]] = scala.math.Ordering$$anon$9@288b8663
    *
    * scala> a.sorted
    * res2: List[List[Int]] = List(List(6, 7, 8, 9), List(1, 2, 3), List(10, 11, 12), List(4, 5))
   */


}
