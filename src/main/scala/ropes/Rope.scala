package ropes

import ropes.instances._

sealed trait Rope {
  def write: String
}
final case class Exactly[V <: Char with Singleton](value: V) extends Rope {
  def write: String = value.toString
}
object Exactly extends ExactlyInstances
object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Parse.Result[R] = parse.parse(s)

  //TODO find a monad for the return type
  def generateArbitrary[R <: Rope](implicit generate: Generate[R]): Iterator[R] = generate.generate
}
