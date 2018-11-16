package ropes

import ropes.instances._

sealed trait Rope

final case class Exactly[V <: Char with Singleton](value: V) extends Rope
object Exactly                                               extends ExactlyInstances

final case class AnyString(value: String) extends Rope
object AnyString                          extends AnyStringInstances

object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Parse.Result[R]  = parse.parse(s)
  def generateArbitrary[R <: Rope](implicit generate: Generate[R]): Iterator[R] = generate.generate

  implicit class RopeOps[R <: Rope](r: R) {
    def write(implicit write: Write[R]): String = write.write(r)
  }
}
