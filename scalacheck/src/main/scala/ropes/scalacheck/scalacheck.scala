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

import org.scalacheck.{Arbitrary, Gen}

package object scalacheck {
  implicit def arbExactlyChar[C <: Char with Singleton](implicit c: ValueOf[C]): Arbitrary[Exactly[C]] =
    Arbitrary(Gen.const(Exactly(c.value)))

  implicit val arbAnyString: Arbitrary[AnyString] = Arbitrary(Arbitrary.arbitrary[String].map(AnyString.apply))

  implicit def arbConcat[P <: Rope: Arbitrary, S <: Rope: Arbitrary]: Arbitrary[Concat[P, S]] =
    Arbitrary(Gen.resultOf(Concat.apply[P, S] _))

  implicit def arbRange[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Arbitrary[Range[Start, End]] =
    Arbitrary(Gen.choose(start.value: Char, end.value: Char).map(Range.unsafeFrom[Start, End]))

  implicit def arbConvertedTo[Source <: Rope, Target](
      implicit arbSource: Arbitrary[Source],
      conversion: Conversion[Source, Target]): Arbitrary[ConvertedTo[Source, Target]] =
    Arbitrary(arbSource.arbitrary.map(conversion.forwards).map(ConvertedTo.apply[Source, Target]))
}
