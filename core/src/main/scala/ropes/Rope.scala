package ropes

import ropes.instances._

sealed trait Rope

final case class Exactly[V <: Char with Singleton](value: V) extends Rope
object Exactly                                               extends ExactlyInstances

final case class AnyString(value: String) extends Rope
object AnyString                          extends AnyStringInstances

final case class Concat[Prefix <: Rope, Suffix <: Rope](prefix: Prefix, suffix: Suffix) extends Rope
object Concat                                                                           extends ConcatInstances

sealed abstract case class Range[Start <: Char with Singleton, End <: Char with Singleton](value: Char) extends Rope
object Range extends RangeInstances {
  def from[Start <: Char with Singleton, End <: Char with Singleton](
      char: Char)(implicit start: ValueOf[Start], end: ValueOf[End]): Option[Range[Start, End]] =
    if (char >= start.value && char <= end.value) Some(new Range[Start, End](char) {})
    else None

  def unsafeFrom[Start <: Char with Singleton: ValueOf, End <: Char with Singleton: ValueOf](
      char: Char): Range[Start, End] =
    from[Start, End](char).getOrElse(throw new IllegalArgumentException(char.toString))
}

final case class ConvertedTo[Source <: Rope, Target](value: Target) extends Rope
object ConvertedTo                                                  extends ConvertedToInstances

object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Parse.Result[R] = parse.parse(s)

  implicit class RopeOps[R <: Rope](r: R) {
    def write(implicit write: Write[R]): String = write.write(r)
  }
}
