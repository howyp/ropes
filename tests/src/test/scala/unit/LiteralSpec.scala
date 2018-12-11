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

class LiteralSpec extends RopeLaws with CommonGens {
  "An `Literal[_]` Rope" - {
    "accepts literal chars" - {
      `obeys Rope laws`[Literal['a']](
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
          Parse[Literal['a']].parse("a" + suffix) should be(Parse.Result.Success(Literal('a'), suffix))
        }
      }
      "Captures the literal type when using .apply(...)" in {
        val _: Literal['a'] = Literal('a')
        """val b: Literal['a'] = Literal('b')""" shouldNot typeCheck
      }
      "Can be created with only a type parameter" in {
        val _: Literal['a'] = Literal['a']
      }
    }
    "does not accept non-singletons" in {
      """Literal[Char]('a')""" shouldNot compile
    }
  }
  "does not accept non-chars (for the moment)" in {
    """Literal["a"]("a")""" shouldNot compile
    """Literal[1](1)""" shouldNot compile
  }
}
