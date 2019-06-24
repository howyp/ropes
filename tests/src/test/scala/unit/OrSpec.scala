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
import ropes.core._
import ropes.scalacheck._
import laws.RopeLaws
import org.scalacheck.{Arbitrary, Gen}

class OrSpec extends RopeLaws with CommonGens {
  "Or[Literal['a'], Literal['b']]" - {
    `obeys Rope laws`[Or[Literal['a'], Literal['b']]](
      genValidStringsWithDecompositionAssertion = Gen.oneOf("a", "b").map {
        case "a" => "a" -> (_ should be(Or.First(Literal('a'))))
        case "b" => "b" -> (_ should be(Or.Second(Literal('b'))))
      },
      genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings =
        Some(Arbitrary.arbitrary[String].suchThat(str => !(str.startsWith("a") || str.startsWith("b"))))
    )
    "Can be created from an Either" in forAll { o: Or[Literal['a'], Literal['b']] =>
      val asEither = o match {
        case Or.First(a)  => Left(a)
        case Or.Second(a) => Right(a)
      }
      Or.from(asEither) should be(o)
    }
  }
}
