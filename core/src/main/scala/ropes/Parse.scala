package ropes

trait Parse[R <: Rope] {
  def parse(string: String): Parse.Result[R]
}
object Parse {

  sealed trait Result[+R <: Rope]
  object Result {
    final case class Complete[R <: Rope](value: R)                      extends Result[R]
    final case class Incomplete[R <: Rope](value: R, remaining: String) extends Result[R]
    case object Failure                                                 extends Result[Nothing]
  }
}
