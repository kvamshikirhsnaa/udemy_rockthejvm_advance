package lectures.part3concurrency

import lectures.part3concurrency.FuturesPromises.mark

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    96
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculates meaning of life on ANOTHER thread
  }

  println(aFuture.value)  // None
  // returns Option[Try[Int]]

  println("Waiting on the future")

  aFuture.onComplete{
    case Success(meaningOfLife) => println(s"meaning of the life is $meaningOfLife")
    case Failure(e) => println(s"i have failed with $e")
  }

  Thread.sleep(2000) // making to sleep MAIN thread to compute child thread result, else it will shutdown JVM


  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // "database of profiles held as Map"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )
    val random = new Random()

    // let's assume out socialNetwork API consists 2 methods
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching from DB
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfid = friends(profile.id)
      Profile(bfid, names(bfid))
    }
  }

  // client: Mark to poke Bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
/*  mark.onComplete{
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete{
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(ex) => ex.printStackTrace()
  }*/

  Thread.sleep(1000) // making to sleep MAIN thread to compute child thread result, else it will shutdown JVM
  // the above code looks very nested and confusing we can make it simple like below

  // functional composition of FUTURES
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)
  // mark is Future[Profile] using map converting to Future[String]

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  // mark is Future[Profile] using flatMap converting to Future[Future[Profile]]

  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))
  // we are restricting marks best friends whose names starts with Z, type is Future[Future[Profile]]


  // if we can write using map, flatMap, filter then we can also write using for-comprehensions

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks

  // recover:
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover{
    case e: Throwable => Profile("fb.id.0-dummy", "forever alone")
  }

  //recoverWith:
  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith{
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.1-zuck")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").
    fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


  // online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from database
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some process
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from DB
      // create a transaction from username to merchantName
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      // Blocking Future to compute result
      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("Saikrishna", "iPhone12", "rock the jvm store", 3000))


  // promises
  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread 1 - consumer
  future.onComplete{
    case Success(r) => println(s"[consumer] I have received $r")
  }

  // thread 2 - producer
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // fulfilling the promise
    promise.success(36)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)








}
