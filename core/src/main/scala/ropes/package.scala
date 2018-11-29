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

import ropes.instances.DigitInstances

package object ropes extends DigitInstances {
  type Digit = Range['0', '9'] ConvertedTo Int
  object Digit {
    def from(value: Int): Either[Rope.InvalidValue.type, Digit] = ConvertedTo.fromTarget(value)
  }

  type OneOrTwoDigits = Concat[Digit, Optional[Digit]] ConvertedTo Int
  object OneOrTwoDigits {
    def from(value: Int): Either[Rope.InvalidValue.type, OneOrTwoDigits] = ConvertedTo.fromTarget(value)
  }
}
