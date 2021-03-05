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

package unit
import gens.CommonGens
import laws.RopeLaws
import org.scalacheck.Gen
import ropes.core._
import ropes.scalacheck._

class DigitSpec extends RopeLaws with CommonGens {
  "A Digit" - {
    val validInts = Gen.choose(0, 9)
    `obeys Rope laws`[Digit](
      genValidStringsWithDecompositionAssertion = validInts.map { digit =>
        digit.toString -> (_.value should be(digit))
      },
      genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings = Some(
        Gen
          .oneOf(
            Gen.choose(Char.MinValue, ('0' - 1).toChar),
            Gen.choose(('9' + 1).toChar, Char.MaxValue)
          )
          .map(_.toString)
      )
    )
    "Can be created from a valid Int" in forAll(validInts) { int =>
      Digit.from(int).getOrElse(fail()).value should be(int)
    }
    "Cannot be created from an invalid Int" in forAll(
      Gen.oneOf(Gen.choose(Int.MinValue, 0), Gen.choose(9, Int.MaxValue))
    ) { int =>
      Digit.from(int) should be(a[Left[_, _]])
    }
  }
}
