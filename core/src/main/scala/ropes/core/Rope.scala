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

/**
  * Super-type of all available format specifications for a strongly typed `String`s.
  */
sealed trait Rope

/**
  * A `Rope` which specifies any number of any characters.
  *
  * Note that this type will only be useful as the final section in a `Rope`, as it will
  * match all subsequent input.
  */
final case class AnyString(value: String) extends Rope
object AnyString                          extends AnyStringInstances

/**
  * A `Rope` which specifies a single literal value.
  *
  * Instances can be created either with a type-level singleton:
  *
  * {{{
  *   val a: Literal['a'] = Literal['a']
  * }}}
  *
  * or with plain value, which will infer the type parameter `V`
  *
  * {{{
  *   val b: Literal['b'] = Literal('b')
  * }}}
  *
  * @tparam V The singleton `Char` which must be matched, expressed as a type.
  * @param value The singleton type `V` expressed as a value.
  */
final case class Literal[V <: Char with Singleton](value: V) extends Rope
object Literal extends LiteralInstances {
  def apply[V <: Char with Singleton](implicit valueOf: ValueOf[V]): Literal[V] = Literal[V](valueOf.value)
}

/**
  *  A `Rope` which specifies that `Prefix` to be present before `Suffix`.
  *
  *  For specifications with more than two concatenated sections, `Concat` should be nested in the `Suffix`, *not* the
  *  `Prefix`. This allows use of the `section` method to flatly access each section in a type-safe manner like so:
  *
  *  {{{
  *  val rope: Concat[Literal['a'], Concat[Literal['b'], Literal['c']]] = ...
  *
  *  val a: Literal['a'] = parsed.section[1]
  *  val b: Literal['b'] = parsed.section[2]
  *  val c: Literal['c'] = parsed.section[3]
  *  }}}
  *
  * @tparam Prefix The `Rope` which appears first in the specification
  * @tparam Suffix The `Rope` which appears second in the specification
  *
  * @param prefix A value which matches the specification for `Prefix`
  * @param suffix A value which matches the specification for `Suffix`
  */
final case class Concat[Prefix <: Rope, Suffix <: Rope](prefix: Prefix, suffix: Suffix) extends Rope {

  /**
    * Provides type-safe access to a numbered section of the concatenation, starting from `1`. Usefull when `Concat`s
    * have been right-nested:
    *
    * {{{
    * val rope: Concat[Literal['a'], Concat[Literal['b'], Literal['c']]] = ...
    *
    * val a: Literal['a'] = parsed.section[1]
    * val b: Literal['b'] = parsed.section[2]
    * val c: Literal['c'] = parsed.section[3]
    * }}}
    *
    * @tparam SectionNumber a singleton-typed `Int` indicating the section required, starting from `1`
    * @return The `SectionNumber`th section, typed according to the specification of `Prefix` or `Suffix` as appropriate
    */
  def section[SectionNumber <: Int with Singleton](
      implicit sectionFinder: SectionFinder[Concat[Prefix, Suffix], SectionNumber]
  ): sectionFinder.Out = sectionFinder(this)
}
object Concat extends ConcatInstances

sealed trait Or[+First <: Rope, +Second <: Rope] extends Rope
object Or extends OrInstances {
  final case class First[F <: Rope](value: F)  extends Or[F, Nothing]
  final case class Second[S <: Rope](value: S) extends Or[Nothing, S]

  def from[F <: Rope, S <: Rope](either: Either[F, S]): F Or S = either match {
    case scala.util.Left(l)  => First(l)
    case scala.util.Right(l) => Second(l)
  }
}

//TODO I'm not really that happy with this name, come up with something better.
sealed abstract case class ConvertedTo[Source <: Rope, Target](value: Target) extends Rope
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

sealed abstract case class Range[Start <: Char with Singleton, End <: Char with Singleton](value: Char) extends Rope
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

sealed abstract case class Repeated[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope](
    values: List[R])
    extends Rope
object Repeated extends RepeatedInstances {
  type Exactly[Reps <: Int with Singleton, R <: Rope] = Repeated[Reps, Reps, R]

  def from[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope](values: List[R])(
      implicit min: ValueOf[MinReps],
      max: ValueOf[MaxReps]): Either[Rope.InvalidValue.type, Repeated[MinReps, MaxReps, R]] =
    Either.cond(
      test = values.size >= min.value && values.size <= max.value,
      right = new Repeated[MinReps, MaxReps, R](values) {},
      left = Rope.InvalidValue
    )

  def unsafeFrom[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope](
      values: List[R])(implicit min: ValueOf[MinReps], max: ValueOf[MaxReps]): Repeated[MinReps, MaxReps, R] =
    from[MinReps, MaxReps, R](values).getOrElse(throw new IllegalArgumentException(values.toString))
}

object Rope {
  def parseTo[R <: Rope](s: String)(implicit parse: Parse[R]): Either[InvalidValue.type, R] =
    parse.parse(s) match {
      case Parse.Result.Complete(r)                             => Right(r)
      case Parse.Result.Incomplete(_, _) | Parse.Result.Failure => Left(InvalidValue)
    }

  case object InvalidValue

  implicit class RopeOps[R <: Rope](r: R) {
    def write(implicit write: Write[R]): String = write.write(r)
  }
}
