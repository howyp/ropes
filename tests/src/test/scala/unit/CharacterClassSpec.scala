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
import ropes.core._
import ropes.core.Spec._
import ropes.scalacheck._

class CharacterClassSpec extends RopeLaws with CommonGens {
  "A CharacterClass" - {
    val genAtoZ    = Gen.choose('a', 'z')
    val genNonAtoZ = Gen
      .oneOf(
        Gen.choose(Char.MinValue, ('a' - 1).toChar),
        Gen.choose(('z' + 1).toChar, Char.MaxValue)
      )
    "['a' - 'z']" - {
      `obeys Rope laws`[CharacterClass['a' - 'z']](
        genValidStringsWithDecompositionAssertion = genAtoZ.map { char =>
          char.toString -> (_.value should be(char))
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genNonAtoZ.map(_.toString))
      )
      "CharacterClass can be created from valid characters" in forAll(genAtoZ) { char =>
        CharacterClass.from['a' - 'z'](char).getOrElse(fail()).value should be(char)
      }
      "CharacterClass cannot be created from invalid characters" in forAll(genNonAtoZ) { char =>
        CharacterClass.from['a' - 'z'](char) should be(a[Left[_, _]])
      }
    }
    "[* &^ ('a' - 'z')]" - {
      `obeys Rope laws`[CharacterClass[* &^ ('a' - 'z')]](
        genValidStringsWithDecompositionAssertion = genNonAtoZ.map { char =>
          char.toString -> (_.value should be(char))
        },
        genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(genAtoZ.map(_.toString))
      )
      "CharacterClass can be created from valid characters" in forAll(genNonAtoZ) { char =>
        CharacterClass.from[* &^ ('a' - 'z')](char).getOrElse(fail()).value should be(char)
      }
      "CharacterClass cannot be created from invalid characters" in forAll(genAtoZ) { char =>
        CharacterClass.from[* &^ ('a' - 'z')](char) should be(a[Left[_, _]])
      }
    }
    "Arbitrary CharacterClass cannot be derived when the reduction is empty" in {
      an[Exception] should be thrownBy Arbitrary.arbitrary[CharacterClass['a' - 'z' &^ ('a' - 'z')]].sample
    }
    "CharacterClass cannot be created when the reduction is empty" in forAll { char: Char =>
      an[Exception] should be thrownBy CharacterClass.from['a' - 'z' &^ ('a' - 'z')](char)
    }
  }
}
