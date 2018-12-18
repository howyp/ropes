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
import ropes.core.{Concat, Literal, Named, Parse}
import ropes.scalacheck._

class NamedSpec extends RopeLaws with CommonGens {
  "A `Name` Rope" - {
    "with a Literal value" - {
      `obeys Rope laws`[Named[Literal['.'], "Delimiter"]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "." -> { parsed =>
            parsed.name should be("Delimiter")
            parsed.value should be(Literal('.'))
          }
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonEmptyString.suchThat(_.head != '.'))
      )
    }
    "Can be found in a Concat by name" - {
      """For a 2-section concat""" in {
        val Parse.Result.Complete(parsed) =
          Parse[Concat[Named[Literal['a'], "The a"], Named[Literal['b'], "The b"]]].parse("ab")
        parsed.section[1].value.value should be('a')
        parsed.section[2].value.value should be('b')
        parsed.section["The a"].value should be('a')
        parsed.section["The b"].value should be('b')
      }
      """For a 3-section concat""" in {
        val Parse.Result.Complete(parsed) =
          Parse[Concat[Named[Literal['a'], "The a"],
                       Concat[Named[Literal['b'], "The b"], Named[Literal['c'], "The c"]]]].parse("abc")
        parsed.section[1].value.value should be('a')
        parsed.section[2].value.value should be('b')
        parsed.section[3].value.value should be('c')
        parsed.section["The a"].value should be('a')
        parsed.section["The b"].value should be('b')
        parsed.section["The c"].value should be('c')
      }
      """For a 4-section concat""" in {
        val Parse.Result.Complete(parsed) =
          Parse[Concat[Named[Literal['a'], "The a"],
                       Concat[Named[Literal['b'], "The b"],
                              Concat[Named[Literal['c'], "The c"], Named[Literal['d'], "The d"]]]]].parse("abcd")
        parsed.section[1].value.value should be('a')
        parsed.section[2].value.value should be('b')
        parsed.section[3].value.value should be('c')
        parsed.section[4].value.value should be('d')
        parsed.section["The a"].value should be('a')
        parsed.section["The b"].value should be('b')
        parsed.section["The c"].value should be('c')
        parsed.section["The d"].value should be('d')
      }
      """Does not compile if the name cannot be found""" in {
        val Parse.Result.Complete(parsed) =
          Parse[Concat[Named[Literal['a'], "The a"], Named[Literal['b'], "The b"]]].parse("ab")
        """parsed.section["The c"]""" shouldNot typeCheck
      }
    }
  }
}
