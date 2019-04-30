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

package ropes

import ropes.core._
import org.scalacheck.{Arbitrary, Gen}

package object scalacheck {
  implicit def arbLiteral[
      C <: Char with Singleton,
      N <: Naming
  ](
      implicit
      c: ValueOf[C]
  ): Arbitrary[Literal[C] { type Name = N }] =
    Arbitrary(Gen.const(Literal(c.value).asInstanceOf[Literal[C] { type Name = N }]))

  implicit val arbAnyString: Arbitrary[
    AnyString
  ] = Arbitrary(Arbitrary.arbitrary[String].map(AnyString.apply))

  implicit def arbConcat[
      P <: Rope: Arbitrary,
      S <: Rope: Arbitrary,
      N <: Naming
  ]: Arbitrary[Concat[P, S] { type Name = N }] =
    Arbitrary(Gen.resultOf(Concat.apply[P, S] _).map(_.asInstanceOf[Concat[P, S] { type Name = N }]))

  implicit def arbRange[
      Start <: Char with Singleton,
      End <: Char with Singleton,
      N <: Naming
  ](
      implicit
      start: ValueOf[Start],
      end: ValueOf[End]
  ): Arbitrary[Range[Start, End] { type Name = N }] =
    Arbitrary(
      Gen
        .choose(start.value: Char, end.value: Char)
        .map(Range.unsafeFrom[Start, End](_).asInstanceOf[Range[Start, End] { type Name = N }]))

  implicit def arbCharacterClass[
      S <: Spec,
      N <: Naming
  ](
      implicit
      reduce: Reduce[S]
  ): Arbitrary[CharacterClass[S] { type Name = N }] =
    Arbitrary((reduce.reduce.map { case (s, b) => Gen.choose(s, b) } match {
      case List()                                   => throw new IllegalStateException("Cannot create a generator for an empty CharacterClass")
      case List(single)                             => single
      case firstRange :: secondRange :: otherRanges => Gen.oneOf(firstRange, secondRange, otherRanges: _*)
    }).map(CharacterClass[S](_).asInstanceOf[CharacterClass[S] { type Name = N }]))

  implicit def arbConvertedTo[
      Source <: Rope,
      Target,
      N <: Naming
  ](
      implicit
      arbSource: Arbitrary[Source],
      conversion: Conversion[Source, Target]
  ): Arbitrary[ConvertedTo[Source, Target] { type Name = N }] =
    Arbitrary(
      arbSource.arbitrary.map(
        ConvertedTo.fromSource[Source, Target](_).asInstanceOf[ConvertedTo[Source, Target] { type Name = N }]))

  //TODO this should be redundant
  implicit def arbOptional[
      R <: Rope,
      N <: Naming
  ](
      implicit
      arb: Arbitrary[R]
  ): Arbitrary[Optional[R] { type Name = N }] =
    Arbitrary(Gen.option(arb.arbitrary).map(Optional.apply[R](_).asInstanceOf[Optional[R] { type Name = N }]))

  implicit def arbRepeated[
      MinReps <: Int with Singleton,
      MaxReps <: Int with Singleton,
      R <: Rope,
      N <: Naming
  ](
      implicit
      min: ValueOf[MinReps],
      max: ValueOf[MaxReps],
      arb: Arbitrary[R]
  ): Arbitrary[Repeated[MinReps, MaxReps, R] { type Name = N }] =
    Arbitrary(
      Gen
        .chooseNum(min.value: Int, max.value: Int)
        .flatMap(Gen.listOfN(_, arb.arbitrary))
        .map(Repeated.unsafeFrom[MinReps, MaxReps, R](_).asInstanceOf[Repeated[MinReps, MaxReps, R] { type Name = N }]))

  implicit def arbOr[
      First <: Rope,
      Second <: Rope
  ](
      implicit
      arbFirst: Arbitrary[First],
      arbSecond: Arbitrary[Second]
  ): Arbitrary[First Or Second] = Arbitrary(
    Gen.oneOf(arbFirst.arbitrary.map(Or.First.apply), arbSecond.arbitrary.map(Or.Second.apply))
  )
}
