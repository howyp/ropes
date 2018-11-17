package ropes

import ropes.instances._

sealed trait Rope

final case class Exactly[V <: Char with Singleton](value: V) extends Rope
object Exactly                                               extends ExactlyInstances

final case class AnyString(value: String) extends Rope
object AnyString                          extends AnyStringInstances

final case class Concat[Prefix <: Rope, Suffix <: Rope](prefix: Prefix, suffix: Suffix) extends Rope
object Concat                                                                           extends ConcatInstances

object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Parse.Result[R] = parse.parse(s)

  implicit class RopeOps[R <: Rope](r: R) {
    def write(implicit write: Write[R]): String = write.write(r)
  }
}
