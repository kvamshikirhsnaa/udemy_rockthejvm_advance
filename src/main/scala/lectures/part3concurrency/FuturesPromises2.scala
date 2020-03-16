package lectures.part3concurrency

import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FuturesPromises2 extends App {

  /*
     Exercises:
     1. fulfill a future immediately with a value
     2. inSequence(fa, fb)
     3. first(fa, fb) returns new Future with the first value of two futures
     4. last(fa, fb) returns new future with the last value of two futures
     5. retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]

  */

  // fulfill a future immediately with a value
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // inSequence(fa, fb)
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = first.flatMap(_ => second )

  // first result out of 2 futures
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  // last result out of 2 futures
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the last future will complete
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    val checkAndComplete = (result: Try[A]) => if (!bothPromise.tryComplete(result)) lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future{
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(f => println("FIRST: " + f)) // FIRST: 42
  last(fast, slow).foreach(l => println("LAST: " + l))  // LAST: 45

  Thread.sleep(1000)


  // retryUntil
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action().
      filter(condition).  // returns Future[A] if condition fails throws exception
      recoverWith{
        case _ => retryUntil(action, condition)
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(200)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => println("settled at " + result))

  Thread.sleep(10000)




}
