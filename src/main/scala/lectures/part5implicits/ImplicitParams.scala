package lectures.part5implicits

object ImplicitParams extends App {

  case class RetryParams(times: Int)

  import scala.util.control.NonFatal

  def retryCall[A](fn: => A, currentTry: Int = 0)(retryParams: RetryParams): A = {
    try fn
    catch {
      case NonFatal(ex) if currentTry < retryParams.times =>
        retryCall(fn, currentTry + 1)(retryParams)
    }
  }

  def retry[A](fn: => A)(implicit retryParams: RetryParams): A =
    retryCall(fn, 0)(retryParams)


  var x = 0

  def checkIt: Int = {
    x = x  + 1
    println(s"checking $x")
    require(x > 4, "x is not enough")
    x
  }

  retry(checkIt)(RetryParams(5))



  implicit val retries: RetryParams = RetryParams(5)


  retry {
    println("trying")
    1 / 0
  }

  import scala.concurrent._
  import ExecutionContext.Implicits.global

  Future(1)


  object Emp {
    implicit val name: String = "Prakash"

  }

  implicit val name: String = "saikrishna"

  def sample(implicit x: String) = s"$x is in kotakonda"

  sample
  sample("prakash")


}
