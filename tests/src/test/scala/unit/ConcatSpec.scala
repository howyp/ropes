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
import org.scalacheck.{Arbitrary, Gen}
import ropes.core.{AnyString, Concat, Exactly}
import ropes.scalacheck._

class ConcatSpec extends RopeLaws with CommonGens {
  "A `Concat` Rope" - {
    "with an Exact prefix and suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], Exactly['b']]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "ab" -> { parsed =>
            parsed.prefix should be(Exactly('a'))
            parsed.suffix should be(Exactly('b'))
            parsed.section[1] should be(Exactly('a'))
            parsed.section[2] should be(Exactly('b'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(
          Gen.oneOf(
            Gen.const(""),
            genNonEmptyString.suchThat(_.head != 'a'),
            genNonEmptyString.suchThat(_.head != 'b').map("a" + _)
          )
        )
      )
    }
    "with an Exact['a'] prefix an AnyString suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], AnyString]](
        genValidStringsWithDecompositionAssertion = Arbitrary
          .arbitrary[String]
          .map { suffix =>
            "a" + suffix -> { parsed =>
              parsed.prefix should be(Exactly('a'))
              parsed.suffix should be(AnyString(suffix))
              parsed.section[1] should be(Exactly('a'))
              parsed.section[2] should be(AnyString(suffix))
            }
          },
        genSuffixToMakeValidStringIncomplete = None,
        genInvalidStrings = Some(
          Gen.oneOf(
            Gen.const(""),
            genNonEmptyString.suchThat(_.head != 'a')
          )
        )
      )
    }
    "with an nested Concats in the Suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], Concat[Exactly['b'], Exactly['c']]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abc" -> { parsed =>
            parsed.section[1] should be(Exactly('a'))
            parsed.section[2] should be(Exactly('b'))
            parsed.section[3] should be(Exactly('c'))
            "parsed.section[4]" shouldNot typeCheck
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abc")))
      )
    }
    "with a deeper nested Concats in the Suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], Concat[Exactly['b'], Concat[Exactly['c'], Exactly['d']]]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abcd" -> { parsed =>
            parsed.section[1] should be(Exactly('a'))
            parsed.section[2] should be(Exactly('b'))
            parsed.section[3] should be(Exactly('c'))
            parsed.section[4] should be(Exactly('d'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abcd")))
      )
    }
    "with a concat in the prefix" - {
      `obeys Rope laws`[Concat[Concat[Exactly['a'], Exactly['b']], Concat[Exactly['c'], Exactly['d']]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abcd" -> { parsed =>
            parsed.section[1] should be(Concat(Exactly('a'), Exactly('b')))
            parsed.section[2] should be(Exactly('c'))
            parsed.section[3] should be(Exactly('d'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abcd")))
      )
    }
  }
}
