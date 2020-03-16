package lectures.part5implicits

object TypeTag extends App {


  import scala.reflect
 //  import scala.reflect.runtime.universe._


/*

  def isA[T: ClassTag](x: Any): Boolean = x match{
    case _: T => true
    case _ => false
  }

  isA[String]("hello")

  isA[Int]("hello")

  isA[String](20)


  isA[Map[String,Int]](Map("hello" -> 1, "world" -> 2))
  isA[Map[String, Int]](Map("hello" -> "foo"))

  //TypeTag

  // ClassTag only provides class tagging(and pattern matching) for the
  // top type level only

  // the above giving wrong results for this we have to use TypeTag
  // for low level type checking

  // TypeTag provides most everything the compiler knows about the type

  val ct = classTag[Map[String, List[Int]]]
  // op: scala.collection.immutable.Map
  // here when we use classTag it will giving just top level type which is Map

  ct.runtimeClass

  val tt = typeTag[Map[String, List[Int]]]
  // op: TypeTag[Map[String,scala.List[Int]]]
  // here when we use typeTag it will giving for all level types

  val theType = tt.tpe  //typeTag has tpe function which will gives actual return type
  // op: Map[String,scala.List[Int]]

  theType.baseClasses // List(trait Map, trait MapLike, trait Map, trait MapLike, .....)
  theType.typeArgs  // List(String, scala.List[Int])

  // types comparing
  theType =:= typeOf[Map[String, Int]]
  theType =:= typeOf[Map[String, List[Int]]]

  typeTag[Map[String, Int]]
  typeTag[Map[String, Int]].tpe



  case class Tagged[A](value: A)(implicit val tag: TypeTag[A])

  val tagged1 = Tagged(Map(1 -> "one", 2 -> "two"))
  val tagged2 = Tagged(Map(1 -> 1, 2 -> 2))

  def taggedIs[A, B](x: Tagged[Map[A,B]]): Boolean = x.tag.tpe match {
    case t if t =:= typeOf[Map[Int, String]] => true
    case _ => false
  }

  taggedIs(tagged1)
  taggedIs(tagged2)

  def taggedIs2[A,B](x: Tagged[Map[A,B]]): Boolean = x.tag.tpe match {
    case t if t =:= typeOf[Map[String, String]] => true
    case _ => false
  }

  taggedIs2(tagged1)
  taggedIs2(tagged2)


  def taggedIs3[A](x: Tagged[A]): Boolean = x.tag.tpe match {
    case t if t =:= typeOf[Map[Int, String]] => true
    case _ => false
  }

  taggedIs3(tagged1)
  taggedIs3(tagged2)



*/


  class Box1[T](item: T)(implicit ev: T =:= Int){
    def double: Int = item * 2
  }

  val b1 = new Box1(10)

  b1.double

  //val b2 = new Box1("hello")


  class Box2[T](item: T){
    def double(implicit ev: T =:= Int): Int = item * 2
  }


  val b2 = new Box2(10)

  b2.double

  val b3 = new Box2("hello")
  //b3.double



}
