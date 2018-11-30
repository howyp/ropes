/*
 * Copyright 2018 Howard Perrin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ropes.core

import instances._

sealed trait Rope

final case class Concat[Prefix <: Rope, Suffix <: Rope](prefix: Prefix, suffix: Suffix) extends Rope {
  def section[SectionNumber <: Int with Singleton](
      implicit sectionFinder: SectionFinder[Concat[Prefix, Suffix], SectionNumber]
  ): Rope = sectionFinder(this)
}
object Concat extends ConcatInstances

sealed trait Section extends Rope

final case class AnyString(value: String) extends Section
object AnyString                          extends AnyStringInstances

final case class Exactly[V <: Char with Singleton](value: V) extends Section
object Exactly                                               extends ExactlyInstances

//TODO I'm not really that happy with this name, come up with something better.
sealed abstract case class ConvertedTo[Source <: Rope, Target](value: Target) extends Section
object ConvertedTo extends ConvertedToInstances {
  def fromTarget[Source <: Rope, Target](target: Target)(
      implicit conversion: Conversion[Source, Target]): Either[Rope.InvalidValue.type, ConvertedTo[Source, Target]] =
    //TODO doing the conversion and throwing the value away feels bad, but if `backwards` was a partial function
    // we'd be doing that anyway
    conversion.backwards(target).map(_ => new ConvertedTo[Source, Target](target) {}).toRight(left = Rope.InvalidValue)

  def fromSource[Source <: Rope, Target](source: Source)(
      implicit conversion: Conversion[Source, Target]): ConvertedTo[Source, Target] =
    new ConvertedTo[Source, Target](conversion.forwards(source)) {}
}

final case class Optional[R <: Rope](value: Option[R]) extends Section
object Optional                                        extends OptionalInstances

sealed abstract case class Range[Start <: Char with Singleton, End <: Char with Singleton](value: Char) extends Section
object Range extends RangeInstances {
  def from[Start <: Char with Singleton, End <: Char with Singleton](char: Char)(
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Either[Rope.InvalidValue.type, Range[Start, End]] =
    if (char >= start.value && char <= end.value) Right(new Range[Start, End](char) {})
    else Left(Rope.InvalidValue)

  def unsafeFrom[Start <: Char with Singleton: ValueOf, End <: Char with Singleton: ValueOf](
      char: Char): Range[Start, End] =
    from[Start, End](char).getOrElse(throw new IllegalArgumentException(char.toString))
}

object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Either[Parse.Result.Failure.type, R] =
    parse.parse(s) match {
      case Parse.Result.Complete(r)                             => Right(r)
      case Parse.Result.Incomplete(_, _) | Parse.Result.Failure => Left(Parse.Result.Failure)
    }

  case object InvalidValue

  implicit class RopeOps[R <: Rope](r: R) {
    def write(implicit write: Write[R]): String = write.write(r)
  }
}
