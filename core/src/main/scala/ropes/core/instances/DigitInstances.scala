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
  implicit val digitConversion: Conversion[Range['0', '9'], Int] = Conversion.instance(
    forwards = _.value.toInt - '0'.charValue(),
    backwards = target => Range.from['0', '9']((target + '0'.charValue()).toChar).swap.map(_ => Conversion.Failed).swap
  )

  implicit def repeatedDigitsConversion[
      MinReps <: Int with Singleton,
      MaxReps <: Int with Singleton,
      N <: Naming
  ](
      implicit
      minReps: ValueOf[MinReps],
      maxReps: ValueOf[MaxReps]
  ): Conversion[Repeated[MinReps, MaxReps, Digit] { type Name = N }, Int] =
    Conversion.instance[Repeated[MinReps, MaxReps, Digit] { type Name = N }, Int](
      forwards = _.values.foldLeft(0) {
        case (accumulated, digit) => accumulated * 10 + digit.value
      },
      backwards = { int =>
        val listOfDigits =
          int.toString.toList
            .map(Range.unsafeFrom['0', '9'](_))
            .map(ConvertedTo.fromSource(_))
        val listOfDigitsPaddedWithZeros =
          List.fill(minReps.value - listOfDigits.size)(Digit.unsafeFrom(0)) ++ listOfDigits

        Right(
          Repeated
            .unsafeFrom[MinReps, MaxReps, Digit](listOfDigitsPaddedWithZeros)
            .asInstanceOf[Repeated[MinReps, MaxReps, Digit] { type Name = N }])
      }
    )
}
