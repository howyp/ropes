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
import ropes.core.{AnyString, Concat, Literal}
import ropes.scalacheck._

class ConcatSpec extends RopeLaws with CommonGens {
  "A `Concat` Rope" - {
    "with an Exact prefix and suffix" - {
      `obeys Rope laws`[Concat[Literal['a'], Literal['b']]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "ab" -> { parsed =>
            parsed.prefix should be(Literal('a'))
            parsed.suffix should be(Literal('b'))
            parsed.section[1] should be(Literal('a'))
            parsed.section[2] should be(Literal('b'))
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
      `obeys Rope laws`[Concat[Literal['a'], AnyString]](
        genValidStringsWithDecompositionAssertion = Arbitrary
          .arbitrary[String]
          .map { suffix =>
            "a" + suffix -> { parsed =>
              parsed.prefix should be(Literal('a'))
              parsed.suffix should be(AnyString(suffix))
              parsed.section[1] should be(Literal('a'))
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
      `obeys Rope laws`[Concat[Literal['a'], Concat[Literal['b'], Literal['c']]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abc" -> { parsed =>
            parsed.section[1] should be(Literal('a'))
            parsed.section[2] should be(Literal('b'))
            parsed.section[3] should be(Literal('c'))
            "parsed.section[4]" shouldNot typeCheck
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abc")))
      )
    }
    "with a deeper nested Concats in the Suffix" - {
      `obeys Rope laws`[Concat[Literal['a'], Concat[Literal['b'], Concat[Literal['c'], Literal['d']]]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abcd" -> { parsed =>
            parsed.section[1] should be(Literal('a'))
            parsed.section[2] should be(Literal('b'))
            parsed.section[3] should be(Literal('c'))
            parsed.section[4] should be(Literal('d'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abcd")))
      )
    }
    "with a concat in the prefix" - {
      `obeys Rope laws`[Concat[Concat[Literal['a'], Literal['b']], Concat[Literal['c'], Literal['d']]]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "abcd" -> { parsed =>
            parsed.section[1] should be(Concat(Literal('a'), Literal('b')))
            parsed.section[2] should be(Literal('c'))
            parsed.section[3] should be(Literal('d'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(!_.startsWith("abcd")))
      )
    }
  }
}
