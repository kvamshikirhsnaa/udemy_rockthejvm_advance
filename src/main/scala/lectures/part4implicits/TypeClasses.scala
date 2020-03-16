package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  val sai = User("saikrishna", 25, "sai@gmail.com")

  println(sai.toHtml)
  // <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div>

  /*
    draw backs
    1. works for types we write
    2. one implementation out of quite a number
  */

  // option 2: PatternMatching

  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n,a,e) =>
      case "ftfhjnkm" =>
      case _ =>
    }
  }

  /*
     draw backs
     1. lost type safety
     2. need to modify code every time
     3. STILL ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = {
      s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
    }
  }

  println(UserSerializer.serialize(sai))
 //  <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div>

  /*
    Advantages:
    1. we can design serializer for other types
   */
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2. we can define multiple serializer for certain types
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = {
      s"<div>${user.name} </div>"
    }
  }

  // part 2

  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = {
      serializer.serialize(value)
    }

    // using this we can control over HTMLSerializer object, if that object has any other methods,
    // we can access them bcuz of factory(apply) method
    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int]{
    override def serialize(value: Int): String = s"<div style: color = blue> $value </div>"
  }

  println(HTMLSerializer.serialize(42)(IntSerializer))
  println(HTMLSerializer.serialize(42))  // IntSerializer is implicit object so we can remove it

  implicit object UserSerializerNew extends HTMLSerializer[User] {
    override def serialize(user: User): String = {
      s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
    }
  }

  println(HTMLSerializer.serialize(sai))
  // <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div>

  println(HTMLSerializer.serialize(sai)(UserSerializer))
  // <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div>

  println(HTMLSerializer.serialize(sai)(PartialUserSerializer))
  // <div>saikrishna </div>


  // we can access the entire type class interface
  println(HTMLSerializer[User].serialize(sai))
  // HTMLSerializer[User] will give UserSerializer, we call serialize method on that

  // part-3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  /*
   object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = {
      serializer.serialize(value)
    }

   Note:
   implementation of object and class both are same
   instead of calling on object we created one implicit class with type parameter

   */

  println(sai.toHTML(UserSerializer))  // println(new HTMLEnrichment(sai).toHTML(UserSerializer)
  println(sai.toHTML)
  // <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div>

  /*
   Advantages:
   1. can extend to new types
   2. choose implementation
 */

  println(25.toHTML)
  //  <div style: color = blue> 25 </div>

  println(sai.toHTML(PartialUserSerializer))
  // <div>saikrishna </div>


  /*
    - type class itself -> HTMLSerializer[T]{...}
    - type class instances (some of which are implicit) -> UserSerializer, IntSerializer, PartialUserSerializer
    - conversion with implicit classes -> HTMLEnrichment[T] {...}
   */

  // context bounds
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String = {
    s"<html><body> ${content.toHTML(serializer)}</body></html>"
  }

  // sugar-coating using context bound

  /**
    * def htmlSugar[T: HTMLSerializer](content: T): String = {
    * s"<html><body> ${content.toHTML}</body></html>"
    * }
    *
    * println(htmlSugar(sai))
   */

  /*
     advantage:
     here T is context bounded as HTMLSerializer
     method code is super readable

     disadvantage:
     we can not use implicit serializer inside method

     to access implicit value inside this method we have to use "implicitly"

   */

  // implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of code
  val standardPerms = implicitly[Permissions]


  // using implicitly
  def htmlSugar[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // s"<html><body> ${content.toHTML(serializer)}</body></html>"
    s"<html><body> ${serializer.serialize(content)}</body></html>"
  }

  println(htmlSugar(sai))
  // <html><body> <div>saikrishna (25 yo) <a href=sai@gmail.com/> </div></body></html>












}
