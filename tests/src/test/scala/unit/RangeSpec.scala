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
import ropes._
import ropes.scalacheck._

class RangeSpec extends RopeLaws with CommonGens {
  "A Range['a','z']" - {
    val genAtoZ = Gen.choose('a', 'z')
    val genNonAtoZ = Gen
      .oneOf(
        Gen.choose(Char.MinValue, ('a' - 1).toChar),
        Gen.choose(('z' + 1).toChar, Char.MaxValue)
      )

    `obeys Rope laws`[Range['a', 'z']](
      genValidStringsWithDecompositionAssertion = genAtoZ.map { char =>
        char.toString -> (_.value should be(char))
      },
      genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings = Some(genNonAtoZ.map(_.toString))
    )
    "Range can be created from valid characters" - forAll(genAtoZ) { char =>
      Range.from['a', 'z'](char).right.get should have('value (char))
    }
    "Range cannot be created from invalid characters" - forAll(genNonAtoZ) { char =>
      Range.from['a', 'z'](char) should be('left)
    }
  }
}
