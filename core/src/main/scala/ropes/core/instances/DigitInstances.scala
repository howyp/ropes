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

package ropes.core.instances

import ropes.core._

private[core] trait DigitInstances {
  //TODO consider if we can write Conversion[Digit]
  implicit val digitConversion: Conversion[Range['0', '9'], Int] = Conversion(
    forwards = _.value.toInt - '0'.charValue(),
    backwards = target => Range.from['0', '9']((target + '0'.charValue()).toChar).toOption
  )

  implicit def oneOrTwoDigitsConversion: Conversion[Digit Concat Optional[Digit], Int] =
    Conversion[Digit Concat Optional[Digit], Int](
      forwards = {
        case Concat(ones, Optional(None))       => ones.value
        case Concat(tens, Optional(Some(ones))) => tens.value * 10 + ones.value
      },
      backwards = int =>
        if (int > 99 || int < 0) None
        else
          Digit.from(int % 10).toOption.map { ones =>
            Digit.from(int / 10 % 10).toOption.filterNot(_.value == 0) match {
              case None       => Concat(ones, Optional(None))
              case Some(tens) => Concat(tens, Optional(Some(ones)))
            }
        }
    )
}
