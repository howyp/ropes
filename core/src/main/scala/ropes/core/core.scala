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

import ropes.core.instances.DigitInstances

package object core extends DigitInstances {
  type Digit = Range['0', '9'] ConvertedTo Int
  object Digit {
    def from(value: Int): Either[Rope.InvalidValue.type, Digit] = ConvertedTo.fromTarget(value)
    def unsafeFrom(value: Int): Digit =
      Digit.from(value).getOrElse(throw new IllegalArgumentException(value.toString))
  }

  type OneOrTwoDigits = Repeated[1, 2, Digit] ConvertedTo Int
  object OneOrTwoDigits {
    def from(value: Int): Either[Rope.InvalidValue.type, OneOrTwoDigits] = ConvertedTo.fromTarget(value)
  }
}
