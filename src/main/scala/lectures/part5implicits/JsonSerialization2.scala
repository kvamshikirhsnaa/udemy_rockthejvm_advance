package lectures.part5implicits

object JsonSerialization2 extends App {

  import java.util.Date

  import scala.util.Try


  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  trait JSONWrite[T] {
    def toJsonString(item: T): String
  }

  def jsonify[T: JSONWrite](item: T): String =
    implicitly[JSONWrite[T]].toJsonString(item)

  //             (OR)

  def jsonify2[T: JSONWrite](item: T) =  {
    val tJson = implicitly[JSONWrite[T]]
    tJson.toJsonString(item)
  }

  implicit object StrJsonWrite extends JSONWrite[String] {
    override def toJsonString(item: String) = s""""$item""""
  }

  jsonify2("Hello")

  implicit object IntJsonfify extends JSONWrite[Int] {
    override def toJsonString(item: Int): String = item.toString
  }

  jsonify(21)

  implicit object DoubleJsonfify extends JSONWrite[Double] {
    override def toJsonString(item: Double): String = item.toString
  }

  jsonify(28.7)


  implicit def listJsonWrite[T: JSONWrite]: JSONWrite[List[T]] = new JSONWrite[List[T]] {
    override def toJsonString(xs: List[T]): String = {
      val tJsonify = implicitly[JSONWrite[T]]
      //  xs.map(x => tJsonify.toJsonString(x)).mkString("[", ",", "]")
      xs.map(x => jsonify(x)).mkString("[", ",", "]")
    }
  }

  jsonify(List("Hello", "World"))

  jsonify(List(1,2,3))
/*

  // here new JSONWrite was not highlighted cuz we can write using single abstract method
  implicit def mapJsonWrite[T: JSONWrite]: JSONWrite[Map[String, T]] = new JSONWrite[Map[String, T]] {
    def toJsonString(m: Map[String, T]): String = {
      val pairs = for ((k, v) <- m) yield
        s"${jsonify(k)}: ${jsonify(v)}"
      pairs.mkString("{\n  ", ",\n  ", "\n}")
    }
  }
*/

  // using single abstract method
  implicit def mapJsonWrite[T: JSONWrite]: JSONWrite[Map[String, T]] =  {
    m: Map[String, T] => {
      val pairs = for ((k, v) <- m) yield
        s"${jsonify(k)}: ${jsonify(v)}"
      pairs.mkString("{\n  ", ",\n  ", "\n}")
    }
  }

  implicit object AnyJsonWrite extends JSONWrite[Any] {
    override def toJsonString(x: Any) = {
      if (Try(x.toString.toInt).isSuccess) x.toString
     // else if (x.isInstanceOf[User]) jsonify(x.asInstanceOf[User])
     // else if (x.isInstanceOf[Post]) jsonify(x.asInstanceOf[Post])
     // else if (x.isInstanceOf[Feed]) jsonify(x.asInstanceOf[Feed])
      else s""""$x""""
    }
  }

  jsonify(Map(
    "hello" -> List("hello", "world"),
    "goodbye" -> List("goodbye", "cruel", "world")
  ))

  implicit object UserJsonWrite extends JSONWrite[User] {
    override def toJsonString(user: User): String = {
      val pairs =  Map(
        "name" -> user.name,
        "age" -> user.age,
        "email" -> user.email
      )
      for ((k, v) <- pairs) yield
        s"${jsonify(k)}: ${jsonify(v)}"
    }.mkString("{", ",", "}")
  }

  implicit object PostJsonWrite extends JSONWrite[Post] {
    override def toJsonString(post: Post): String = {
      val pairs = Map(
        "content" -> post.content,
        "createdAt" -> post.createdAt
      )

      for ((k,v) <- pairs) yield
        s"${jsonify(k)}:${jsonify(v.toString)}}"
    }.mkString("{", ",", "}")
  }

/*

  implicit object FeedJsonWrite extends JSONWrite[Feed] {
    override def toJsonString(feed: Feed): String = {
      val pairs = Map(
        "user" -> feed.user,
        "posts" -> feed.posts.map(x => jsonify(x))
      )

      for ((k,v) <- pairs) yield
        s"${jsonify(k)}:${v}"
    }.mkString("{", ",", "}")
  }

*/

  val sai = User("Saikrishna", 25, "ksai@gmail.com")
  println(jsonify(sai))

  val gvk = User("GVK", 29, "gvk@rockthejvm.com")
  println(jsonify(gvk))


  val now = new Date(System.currentTimeMillis())
  val feed = Feed(gvk, List(
    Post("hello", now),
    Post("look at this cute puppy", now)
  ))


  // println(jsonify(feed))


  val a: Any = "f"

  Try(a.toString.toInt).isSuccess

}
