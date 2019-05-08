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

import ropes.core.instances.{DigitInstances, OptionalInstances, RangeInstances}
import ropes.core.Spec._

package object core extends DigitInstances with OptionalInstances {
  type Letter = Letter.Uppercase Or Letter.Lowercase
  object Letter {
    type Uppercase = Range['A', 'Z']
    type Lowercase = Range['a', 'z']
  }

  type Digit = Range['0', '9'] ConvertedTo Int
  object Digit {
    def from(value: Int): Either[Rope.InvalidValue.type, Digit] = ConvertedTo.fromTarget(value)
    def unsafeFrom(value: Int): Digit =
      Digit.from(value).getOrElse(throw new IllegalArgumentException(value.toString))
  }

  type Optional[R <: Rope] = Repeated[0, 1, R] ConvertedTo Option[R]
  object Optional {
    //TODO should distinguish between partial and complete conversions, to mean that we don't need this .getOrElse
    def apply[R <: Rope](option: Option[R]): Optional[R] =
      ConvertedTo.fromTarget[Repeated[0, 1, R], Option[R]](option).getOrElse(throw new IllegalStateException())
  }

  /**
    * A `Rope` which holds a single character matching a given range.
    * @tparam Start A singleton `Char` type which is the minimum allowable character, inclusive
    * @tparam End A singleton `Char` type which is the maximum allowable character, inclusive
    */
  type Range[Start <: Char with Singleton, End <: Char with Singleton] = CharacterClass[Start - End]
  object Range extends RangeInstances {
    def from[Start <: Char with Singleton: ValueOf, End <: Char with Singleton: ValueOf](
        char: Char): Either[Rope.InvalidValue.type, Range[Start, End]] =
      CharacterClass.from[Start - End](char)

    def unsafeFrom[Start <: Char with Singleton: ValueOf, End <: Char with Singleton: ValueOf](
        char: Char): Range[Start, End] =
      from[Start, End](char).getOrElse(throw new IllegalArgumentException(char.toString))
  }

  type Named[R <: Rope, N <: String with Singleton] = R { type Name = Naming.Assigned[N] }
}
