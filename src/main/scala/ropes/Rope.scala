package ropes

import ropes.instances._

sealed trait Rope {
  def write: String
}
//TODO only allow actual singleton types for `Literal`
final case class Exactly[Literal](value: Literal) extends Rope {
  def write: String = ???
}
object Exactly extends ExactlyInstances
object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Parse.Result[R] = parse.parse(s)

  //TODO find a monad for the return type
  def generateArbitrary[R <: Rope]: Iterator[R] = ???
}
