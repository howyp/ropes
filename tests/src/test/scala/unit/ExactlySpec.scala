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

class ExactlySpec extends RopeLaws with CommonGens {
  "An `Exactly[_]` Rope" - {
    "accepts literal chars" - {
      `obeys Rope laws`[Exactly['a']](
        genValidStringsWithDecompositionAssertion = Gen.const("a").map { str =>
          str -> { parsed =>
            parsed.value should be('a')
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(Gen.oneOf(Gen.const(""), genNonEmptyString.suchThat(_.head != 'a')))
      )
      "Can be parsed when incomplete" in forAll { suffix: String =>
        whenever(suffix.nonEmpty) {
          Parse[Exactly['a']].parse("a" + suffix) should be(Parse.Result.Success(Exactly('a'), suffix))
        }
      }
      "Captures the literal type when using .apply(...)" in {
        val a: Exactly['a'] = Exactly('a')
        """val b: Exactly['a'] = Exactly('b')""" shouldNot typeCheck
      }
    }
    "does not accept non-singletons" in {
      """Exactly[Char]('a')""" shouldNot compile
    }
  }
  "does not accept non-chars (for the moment)" in {
    """Exactly["a"]("a")""" shouldNot compile
    """Exactly[1](1)""" shouldNot compile
  }
}
