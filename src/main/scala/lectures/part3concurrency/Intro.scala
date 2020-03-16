package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  // JVM threads

  /* // Thread is a class in java it takes Runnable interface as parameter
     interface Runnable {
     public void run()
     }
   */

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  /*
    to run any thread we can call start method on thread instance
    start method internally calls run method
   */

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // creates a JVM thread over OS thread

  aThread.join() // blocks until a Thread finishes running

/**

  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val aThreadNew = new Thread(runnable) // here runnable calls run method on main thread only so
  aThreadNew.start() // this is not running in parallel
*/

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
  threadHello.start()
  threadGoodbye.start()
  // different runs produce different results!


  /*
    threads are very expensive to start and kill,
    so java provides a feature of reusing threads using executors and thread pools
   */
  // executors
  val pool = Executors.newFixedThreadPool(10)

  pool.execute(() => println("Something in the thread pool"))
  /*
      pool.execute(Runnable) takes Runnable interface(trait) as argument
     () => println("Something in the thread pool") this is runnable as lambda function
     this runnable will executed by one of the 10 threads managed by this thread pool
     here thread starting is taken care by pool
   */
  pool.execute( () => {
    Thread.sleep(1000)
    println("done after 1 second")
  })

  pool.execute( () => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  pool.shutdown()

  // here done after 1 second and almost done print same time cuz 2 threads are running in parallel
  // done after 2 seconds print after 1 second

  /**
  // shutdown if we call shutdown on pool
  pool.shutdown() // it shutdown all threads in pool
  // once shutdown called no more actions can be submitted to the pool

    println(pool.isShutdown) // true but old threads are running in backend

   pool.execute(() => println("should not appear")) // java.util.concurrent.RejectedExecutionException:
  // it throws exception on calling thread(main thread), that is why even after exception we
  // get old thread result but after this line it won't take new actions.
  println("hello, world") // won't print
   */

  // pool.shutdownNow() // it interrupts the threads that are currently running under the pool
                     // if they are sleeping they throw exception

  /*
    shutdown() will shutdown the pool, but under pool any threads are runnning will run, new actions can't assign to pool
    shutdownNow() will shutdown pool and threads that are currently running
   */



}
