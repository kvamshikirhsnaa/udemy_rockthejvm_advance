package lectures.part2afp

object Monads extends App {

  // our own Try monad

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
     left-identity:
     --------------
     unit.flatMap(f) == f(x)
     Attempt(x).flatMap(f) = f(x)
     // if x throws an exception then Attempt(x) will be Fail, flatMapping Fail will Fail
     // for Success case only it will give f(x)

     Success(x).flatMap(f) = f(x) // proved

     right-identity:
     ---------------
     Attemp.flatMap(Unit) == Attempt
     Success(x).flatMap(x => Attempt(x)) = Attempt(x)
                                         = Success(x)
     Fail(e).flatMap(...) = Fail(e)

     associativity:
     --------------
     Attempt.flatMap(f).flatMap(g) == Attempt.flatMap(x => f(x).flatMap(g))
     Fail(e).flatMap(f).flatMap(g) = Fail(e)
     Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

     Success(x).flatMap(f).flatMap(g) = f(x).flatMap(g) OR Fail(e)
     Success(x).flatMap(x => f(x).flatMap(g)) = f(x).flatMap(g) OR Fail(e)

   */

  val attempt = Attempt {
    throw new RuntimeException("My own monad, Yes!")
  }

  println(attempt) // Fail(java.lang.RuntimeException: My own monad, Yes!)

  /**
    Exercise:
     1.implement a lazy[T] monad = computation which will only be executed when it's needed
       unit/apply in lazy trait
       flatMap in lazy trait

     2. monads = unit + flatMap
        monads = unit + map + flatMap

   */

  // lazy monad
  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value
    def use: A = internalValue
    def flatMap[B](f:(=> A) => Lazy[B]): Lazy[B] = f(internalValue)
   // def sample(f: A => Int): Int = f(value)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    6
  }

  println(lazyInstance) // lectures.par2afp.Monads$Lazy@3f3afe78, this is lazy so println stmt not printed
  println(lazyInstance.use) // Today I don't feel like doing anything
                            // 6

// val z = (x: Int) => x + 2
//  println(lazyInstance.sample(z)) // Today I don't feel like doing anything
                                    // 8

  // def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(value)
  // val flatMappedInstance = lazyInstance.flatMap(x => Lazy{10 * x}) // Today I don't feel like doing anything
  // cuz it tries to evaluate f(value) eagerly
  // to avoid f argument make as call by name

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy{10 * x})
  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy{10 * x})
  println(flatMappedInstance.use) // 60
  println(flatMappedInstance2.use)// 60

  /*
    left-identity:
    --------------
    unit.flatMap(f) = f(v)
    Lazy(v).flatMap(f) = f(v)

    right-identity:
    ---------------
    l.flatMap(unit) = l
    Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

    associativity: l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
    --------------
    Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
    Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)

   */

  /*
     2. map and flatten in terms of flatMap
       Monad[T] {
          def flatMap[B](f: T => Monad[B]): Monad[B] = ..... (implemented)
          def map[B](f: T => Monad[B]): Monad[B] = flatMap(x => unit(f(x))) // Monad[B]
          def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)  // Monad[T]

          List(1,2,3).map(_ * 2) = flatMap(x => List(x * 2)) = List(2,4,6)
          List(List(1,2), List(3,4)).flatten
               = List(List(1,2), List(3,4)).flatMap(x => x) = List(1,2,3,4)
       }
   */


/*
  MONADS:
  -------
  Left identity:
  --------------
  If you have a box (monad) with a value in it and a function that takes the same type of
  value and returns the same type of box, then flatMapping it on the box or just simply applying
  it to the value should yield the same result.

  Take scala’s Option for example

   val value = 1
   val option = Some(value)
   val f: (Int => Option[Int]) = x => Some(x * 2)

   option.flatMap(f) == f(value)
   Some(2) == Some(2)


   Right identity:
   ---------------
   If you have a box (monad) with a value in it and you have a function that takes
   the same type of value and wraps it in the same kind of box untouched, then after
   flatMapping that function on your box should not change it.

   Again, with scala’s Option

   val option = Some(1)

   option.flatMap(Some(_)) == option  // true
   Some(1) == Some(1)


   Associativity:
   --------------
   If you have a box (monad) and a chain of functions that operates on it as the previous
   two did, then it should not matter how you nest the flatMappings of those functions.

   Again, see what it looks like with Option

   val option = Some(1)
   val f: (Int => Option[Int]) = x => Some(x * 2)
   val g: (Int => Option[Int]) = x => Some(x + 6)

   option.flatMap(f).flatMap(g) == option.flatMap(f(_).flatMap(g))
   Some(8)  == Some(8)












*/











}
