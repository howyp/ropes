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

import ropes.core.instances.{DigitInstances, OptionalInstances}

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

  type Named[R <: Rope, N <: String with Singleton] = R { type Name = Naming.Assigned[N] }
}
