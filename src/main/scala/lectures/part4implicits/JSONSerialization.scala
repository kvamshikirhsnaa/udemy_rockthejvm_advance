package lectures.part4implicits

import java.util.Date

import lectures.part4implicits.JSONSerialization.JSONNumber

object JSONSerialization extends App {


  /*
     Users, Posts, Feeds
     serialize to JSON
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1. Intermediate DATA TYPES: Int, String, List, Date
    2. type classes for conversion
    3. serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(str: String) extends JSONValue {
    override def stringify: String = "\"" + str + "\""
  }

  final case class JSONNumber(num: Int) extends JSONValue {
    override def stringify: String = num.toString
  }

  final case class JSONArray(lst: List[JSONValue]) extends JSONValue {
    override def stringify: String = lst.map(_.stringify).mkString("[", ",","]")
  }

  final case class JSONObject(pairs: Map[String, JSONValue]) extends JSONValue {
    /*
       {
         name: "Saikrishna"
         age: 25
         friends: [ ... ]
         latestPost: {
            content: " hello"
            date: ...
            }
       }
       key is String, and value might be String, array, another JSONObject
     */
    override def stringify: String = pairs.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")

  }

  val data = JSONObject(Map(
    "user" -> JSONString("Vamshikrishna"),
    "posts" -> JSONArray(List(
      JSONString("Scala rocks!"),
      JSONNumber(21)
    ))
  ))

  println(data.stringify)
  // {"user":"Vamshikrishna","posts":["Scala rocks!",21]}


  // type class
  /*
    1. type class
    2. type class instances(implicit)
    3. pimp library to use type class instances

   */

  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 2.3 implicit conversion
  implicit class JSONOps[T](value: T){
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = {
      converter.convert(value)
    }
  }

  // 2.2
  // for existing data type (String, Int)
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

//  implicit object StringConverterNew extends JSONConverter[String] {
//    override def convert(value: String): JSONValue = JSONString(value + "lalalala")
//  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // for custom data types(Users, Posts, Feeds)
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> user.name.toJSON,  // JSONString(user.name)
      "age" -> user.age.toJSON,   // JSONNumber(user.age)
      "email" -> user.email.toJSON  // JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> post.content.toJSON,  // JSONString(post.content)
      "createdAt" -> post.createdAt.toString.toJSON  // JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
     /* "user" -> UserConverter.convert(feed.user), // TODO
      "posts" -> JSONArray(feed.posts.map(PostConverter.convert)) // TODO*/
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }



  // call stringify on result
  val now = new Date(System.currentTimeMillis())
  val gvk = User("GVK", 29, "gvk@rockthejvm.com")
  val feed = Feed(gvk, List(
    Post("hello", now),
    Post("look at this cute puppy", now)
  ))

  println(feed.toJSON)
/*  JSONObject(Map(user -> JSONObject(Map(name -> JSONString(GVK), age -> JSONNumber(29),
    email -> JSONString(gvk@rockthejvm.com))), posts -> JSONArray(List(JSONObject(Map(content -> JSONString(hello),
    createdAt -> JSONString(Thu Mar 05 16:08:22 IST 2020))),
  JSONObject(Map(content -> JSONString(look at this cute puppy), createdAt ->
    JSONString(Thu Mar 05 16:08:22 IST 2020)))))))  */


  println(feed.toJSON.stringify)
/*  {"user":{"name":"GVK","age":29,"email":"gvk@rockthejvm.com"},"posts":[{"content":"hello",
    "createdAt":"Thu Mar 05 16:08:22 IST 2020"},{"content":"look at this cute puppy",
    "createdAt":"Thu Mar 05 16:08:22 IST 2020"}]}  */

  def toJsonNew[T: JSONConverter](value: T): String = {
    val converter = implicitly[JSONConverter[T]]
    converter.convert(value).stringify
  }

  println(toJsonNew(feed))
  /*  {"user":{"name":"GVK","age":29,"email":"gvk@rockthejvm.com"},"posts":[{"content":"hello",
    "createdAt":"Thu Mar 05 16:08:22 IST 2020"},{"content":"look at this cute puppy",
    "createdAt":"Thu Mar 05 16:08:22 IST 2020"}]}  */




}
