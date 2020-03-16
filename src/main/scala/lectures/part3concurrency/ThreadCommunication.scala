package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

   /*
     the producer-consumer problem

     producer -> [x] -> consumer

    */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int)= value = newValue

    def get = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting")

      while (container.isEmpty){
        println("[consumer] actively waiting")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing")
      Thread.sleep(500)
      val value = 36
      println("[producer] I have produced after a long work " + value)
      container.set(value)
    })


    consumer.start()
    producer.start()

  }

  // naiveProdCons()


  /*
     thread1:
     --------
     Synchronized:
     -------------
     Entering a synchronized expression on object locks the object.

     val someObj = "hello"
     someObj.synchronized {  // it locks the object's monitor
       .......
     }

     monitor: monitor is a data structure that it's internally used by the JVM to keep
     track of which object is locked by which thread. once you have locked the object any
     other thread trying to evaluate the same expression will block until you're done evaluating
     and when you're done you will release the lock and any other thread is free to evaluate the
     expression if it reaches that point

     *** only "AnyRef" can have synchronized blocks, primitive type Int or Boolean they don't have
     synchronized expressions

     General Principles:
     -------------------
     1. make no assumption about who gets the lock first
     2. keep locking to a minimum
     3. maintain thread safety at ALL times in parallel applications.

  */


  /*
    wait() and notify():
    --------------------
    wait-ing an object's moniter suspends the calling thread indefinitely

    ex:

    thread 1:
    ---------
    val someObj = "hello"
    someObj.synchronized {      <-- lock the object's moniter
     // .... code part 1
     someObj.wait()             <-- release the lock and wait
     // .... code part 2        <-- when allowed to proceed, lock the moniter again and continue
    }

    thread 2:
    ---------
    someObj.synchronized {      <-- lock the object's moniter
    // ... code part 1
    someObj.notify()            <-- when it calls notify() it will give the signal to one of the
                                    sleeping threads that are waiting on this object, that they
                                    may continue after they acquire the lock on the monitor again
                                    but we don't know which thread will execute from sleeping threads
                                    if you want signal all the running threads that they may continue
                                    use notifyAll()
    // ... code part 2
    }                           <-- but only after i am done and unlock the monitor


    *** wait(), notify() and notifyAll() are only allowed within synchronized expressions otherwise
    they will crash program

    * this is whole JVM thread communication
  */



  // wait() and notify()
  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[cosnumer] waiting...")

      container.synchronized{
        container.wait()  // <- here consumer thread will release the lock on container
                          //    and it will be suspended, until someone else namely the producer
                          //    will be signalled that they may continue


        // at this point container must have value
        println("[consumer] I have consumed " + container.get)
      }




    })

    val producer = new Thread( () => {
      println("[producer] hard at work...")
      Thread.sleep(2000)
      val newValue = 36

      container.synchronized{
        println("[producer] I'm producing " + newValue)
        container.set(newValue)
        container.notify()
      }
    })

    consumer.start()
    producer.start()

  }


  //smartProdCons()

  /*
     producer -> [? ? ?] -> consumer
   */
  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random

      while (true) {
        buffer.synchronized{
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least One value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // hey producer there is empty space available, are you lazy
          buffer.notify() // calls producer thread
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread( () => {
      val random = new Random
      var i = 0

      while (true) {
        buffer.synchronized{
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()  // consumer will start working
          }
          // there must be at least one empty space in the buffer
          println("[producer] producing " + i)
          buffer.enqueue(i)

          // hey consumer buffer is full, are you lazy
          buffer.notify()  // calls consumer thread

          i += 1
        }
        Thread.sleep(random.nextInt(1500))
      }
  })
    consumer.start()
    producer.start()
  }

 // prodConsLargeBuffer()


  /*
    Prod-cons, level-3

    producer1 -> [? ? ?] -> consumer1
    producer2 ----^    ---> consumer2

   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random

      /*
          producer produces value, two cons are waiting,
          notifies one consumer
       */
      while (true) {
        buffer.synchronized{
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least One value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed " + x)


          buffer.notifyAll() // notifies buffer
                          // notifies the other consumer
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {

    override def run(): Unit = {
      val random = new Random
      var i = 0

      while (true) {
        buffer.synchronized{
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()  // consumer will start working
          }
          // there must be at least one empty space in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notifyAll()

          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())

  }

  multiProdCons(3,3)



}
