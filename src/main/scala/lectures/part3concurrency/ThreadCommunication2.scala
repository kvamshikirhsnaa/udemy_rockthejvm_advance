package lectures.part3concurrency

object ThreadCommunication2 extends App {

  /*
     Exercises:
     1. think of an example where notifyAll() acts in different way than notify?
     2. create a deadlock
     3. create a livelock

   */

  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized{
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread( () => {
      Thread.sleep(2000)
      println("[announcer] Rock n Roll")
      bell.synchronized{
        //bell.notify()  // only one thread will be waken
        bell.notifyAll() // it will wake all sleeping threads
      }

    }).start()
  }

  //testNotifyAll()

  // exercise2: deadlock
  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized{
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized{
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def swithSide(): Unit = {
      if (side == "right") side == "left"
      else side == "right0"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: oh, but please , $other, feel free to pass...")
        Thread.sleep(1000)
      }
    }
  }

  val eshwar = Friend("Eshwar")
  val sudheer = Friend("Sudheer")

  // new Thread( () => eshwar.bow(sudheer)).start()  // eshwar's lock, then sudheer's lock
  // new Thread( () => sudheer.bow(eshwar)).start()  // sudheer's lock, then eshwar's lock


  // 3: livelock
  new Thread( () => eshwar.pass(sudheer)).start()
  new Thread( () => sudheer.pass(eshwar)).start()



}
