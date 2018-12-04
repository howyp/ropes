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
import org.scalacheck.Arbitrary.arbitrary
import ropes.core._
import ropes.scalacheck._

class RepeatedSpec extends RopeLaws with CommonGens {
  "A Repeated[1,3,Range['a', 'z']]" - {
    def genList(numberOfItems: Gen[Int]) = numberOfItems.flatMap(Gen.listOfN(_, arbitrary[Range['a', 'z']]))
    `obeys Rope laws`[Repeated[1, 3, Range['a', 'z']]](
      genValidStringsWithDecompositionAssertion = genList(Gen.choose(1, 3))
        .map(list => list.map(_.value).mkString("") -> (_.values should be(list))),
      genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString.suchThat(!_.head.isLetter)),
      genInvalidStrings = Some(genNonEmptyString.suchThat(!_.head.isLetter))
    )
    "Can be created from valid lists of characters" in
      forAll(genList(Gen.choose(1, 3))) { list =>
        Repeated.from[1, 3, Range['a', 'z']](list).getOrElse(fail()).values should be(list)
      }
    "Cannot be created from invalid lists of characters" in
      forAll(genList(Gen.oneOf(Gen.const(0), Gen.choose(4, 10)))) { list =>
        Repeated.from[1, 3, Range['a', 'z']](list) should be(Left(Rope.InvalidValue))
      }
  }
}
