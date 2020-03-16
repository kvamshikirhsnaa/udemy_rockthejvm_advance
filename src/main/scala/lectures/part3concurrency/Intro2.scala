package lectures.part3concurrency

object Intro2 extends App {

  def runInParallel = {
    var x = 0

    val thread1 = new Thread( () => {
      x = 1
    })

    val thread2 = new Thread( () => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)
  }

 // for (_ <- 1 to 10) runInParallel

  /*
     for (_ <- 1 to 1000) runInParallel
     most of the time x value will be zero, cuz before getting any thread result x value will be
     printed for every run, only in very rare case we may get 1 or 2 for x, before printing main thread
     may be some one child thread change x value

     this is "race-condition" problem in concurrency
   */


  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
   // println(s"your account has $account rupees")
    account.amount -= price
   // println(s"you have bought a $thing")
   // println(s"your account got deducted $price, now your account balance is $account ")
  }

/*  for (_ <- 1 to 10000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread( () => buy(account, "Shoes", 3000 ))
    val thread2 = new Thread( () => buy(account, "Watch", 8000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 39000) println("AHA: " + account.amount )
  }*/

  /*
    thread1(shoes): 50000
      - account = 50000 - 3000 = 47000
    thread2(watch): 50000
      - account = 50000 - 8000 = 42000 overrides previous account amount


    Some time above code return "AHA: 47000" or "AHA: 42000" or "AHA: 50000"
    that means a user purchased 2 items and might be deducted for only shoes or watch or did't deducted any amount
    this is race condition, this problem very bad in banking and financial sector to avoid
    scala has 2 features

   */

  // option #1: use synchronized()

  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized{
      // no two threads can evaluate this at the same time
     // println(s"your account has $account rupees")
      account.amount -= price
     // println(s"you have bought a $thing")
     // println(s"your account got deducted $price, now your account balance is $account ")
    }
  }

  // this time it won't print anything cuz threads
/*  for (_ <- 1 to 10000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread( () => buySafe(account, "Shoes", 3000 ))
    val thread2 = new Thread( () => buySafe(account, "Watch", 8000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 39000) println("AHA: " + account.amount )
  }*/


  // option #2: use @volatile key word to var or val, means all reads and writes to it are synchronized
  // like below

  /*
    class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }
   */

  /**
    * Exercise 1:
    * cosutruct 50 "inception" threads
    * Thread1 -> Thread2 -> Thread3 -> ...
    * println(hello from thread(num)
    * in reverse order
    * */


  def inceptionThreads(maxThreads: Int, ind: Int = 1): Thread = new Thread( () => {
    if (ind < maxThreads) {
      val newThread = inceptionThreads( maxThreads, ind + 1 )
      newThread.start()
      newThread.join() // wait for thread to finish
    }
    println( s"hello from thread $ind" )
  })

 // inceptionThreads(50).start()

  /*
    * Exercise 2:
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread( () => x += 1))
  threads.foreach(x => x.start())

  // 1. what is the biggest value possible for x
  // 2. what is the smallest value possible for x


  threads.foreach(x => x.join())
  /*
   thread1 reads x as 0
   thread2 reads x as 0
     ....
   thread100 reads x as 0

   for all threads: x might be 1 and write it back to x as 1
   what is the smallest value possible for x = 1
   it occurs very very rarely for all threads as 1

 */
  // if we use join always x value will be mostly 100, sometimes 99, some rare cases 98
  // sometimes very rare b/w 1 to 99,
  println(x)



  /*
    Exercise 3: sleep fallacy
  */

  var messgae = ""
  val awesomeThread = new Thread( () => {
    Thread.sleep(1000)
    messgae = "Scala is awesome"
  })

  messgae = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(10)  // MAIN thread is sleeping for 10
  awesomeThread.join() // wait for awesomeThread to join

  println(messgae) // Scala is awesome

  /*
     what's the value of message?
     is it guaranteed? why? why not?
   */

  /*
     value of message almost always Scala is awesome

     is it guaranteed ? No!

     why? why not:
       messgae = "Scala sucks"
       awesomeThread.start() {
       sleep() - relieves execution
     (awesomeThread)
       sleep() - relieves execution
      (OS gives the CPU to some important thread - takes CPU for more than 2 seconds
      (OS gives the CPU back to MAIN thread)
        println("Scala sucks")
      (OS gives the CPU to awesomeThread)
        it will change messgae value from "Scala sucks" to "Scala is awesome"
        (it is to late cuz println("Scala sucks") printed already
        so sometimes "Scala sucks" is possible but mostly return "Scala is awesome"

     ** sleeping does not guarantee that a thread will sleep exactly the no.of milliseconds
        it will just yield the execution of the CPU to the OS for at least that no.of milliseconds.


   */

  /*
     how to fix this:
        synchronize doesn't work here, synchronize work for concurrent modification
        if two threads are attempting to modify the same message at same time then synchronize can work.
   */




}
