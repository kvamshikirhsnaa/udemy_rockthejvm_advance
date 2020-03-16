package lectures.part5implicits

import scala.reflect.ClassTag

object EventuallyCheck extends App {

  import scala.util.control.NonFatal

  object Eventually {

    case class MaxTries(value: Int)

    case class Interval(value: Int)


    implicit val maxTriesForEvaluating: MaxTries = MaxTries(10)
    implicit val intervalForEvaluating: Interval = Interval(1000)

    def eventually(f: => Int)(implicit maxTries: MaxTries, interval: Interval) {
      eventuallyWith(maxTries.value, interval.value)(f)
    }

    var x = 0
    def checkIt: Int = {
      x = x  + 1
      println(s"checking $x")
      require(x > 5, "x is not enough")
      println(s"I am the one $x")
      x
    }

    def eventuallyWith(maxTries: Int, interval: Int)(f: => Int): Unit = {
      var failedCount = 0
      var succeeded = false
      var optEx: Option[Throwable] = None
      while (failedCount < maxTries && !succeeded) {
        optEx =
          try {
            f
            None
          }
          catch {
            case e: Exception => Some(e)
          }
        succeeded = optEx.isEmpty
        if (!succeeded) {
          failedCount += 1
          Thread.sleep(interval)
        }
      }
      if (optEx.isDefined)
        throw optEx.get

    }
  }

  import Eventually._

  eventually(checkIt)

  eventually {
    println( "trying" )
    1 / 0
  }






}
