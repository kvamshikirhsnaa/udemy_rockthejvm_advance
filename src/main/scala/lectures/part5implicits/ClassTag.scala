package lectures.part5implicits

object ClassTag extends App {

  import scala.reflect._

  val s: String = "hello"

  s.getClass

  //classTag is scala class it will take type and give the
  // type of that class

  val z: Any = "hey"

  z.getClass

  classOf[String]

  val stringClassTag = classTag[String]

  def getClassTag[T: ClassTag](x: T): ClassTag[T] = classTag[T]

  getClassTag("hello")

  getClassTag(z)
  getClassTag(z).runtimeClass

  getClassTag(s)
  getClassTag(s).runtimeClass

  val strCt = getClassTag("hello")

  strCt.runtimeClass

  getClassTag(10)

  getClassTag(10).runtimeClass


  val intCt = getClassTag(10)

  val intArr = intCt.newArray(5) // it gives exactly Array[Int]

  val strArr = strCt.newArray(5)  // it gives exactly Array[String]

  // these class tags are useful for pattern match

  def isA[T](x: Any): Boolean = x match {
    case _: T => true
    case _ => false
  }

  isA[Int](10)

  isA[Int]("hello")

  //Warning:(43, 16) abstract type pattern T is unchecked since it is eliminated by erasure
  //case _:T => true

  // it gives warning bcuz it removes type T cuz of unknown type
  // the above code give wrong result cuz T is unchecked means it removed by complier
  // cuz it does't know what is the exact type of T so as in our code
  // first case is _ whatever the input it will execute so in both cases above
  // we got true, so we need to make use of class tags to get accurate results

  def isA2[T: ClassTag](x: Any): Boolean = x match{
    case _: T => true
    case _ => false
  }

  isA2[String]("hello")

  isA2[Int]("hello")

  isA2[String](20)

  isA2[Int](10)

  val k: Any = "hey"

  isA2[String](k)



  def isA3[T](x: Any)(implicit ct: ClassTag[T]): Boolean = x match{
    case _: T => true
    case _ => false
  }

  isA3[String]("hello")

  isA3[Int]("hello")

  isA3[String](20)

  isA3[Int](10)

  val k3: Any = "hey"

  isA3[String](k3)



}
