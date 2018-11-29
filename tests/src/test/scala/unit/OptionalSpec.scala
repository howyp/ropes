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

class OptionalSpec extends RopeLaws with CommonGens {
  "An Optional[Exactly['x']" - {
    type Opt = Optional[Exactly['x']]
    `obeys Rope laws`[Opt](
      genValidStringsWithDecompositionAssertion = Gen.oneOf(
        "x" -> { opt: Opt =>
          opt.value should be(Some(Exactly('x')))
          ()
        },
        "" -> { opt: Opt =>
          opt.value should be(None)
          ()
        }
      ),
      genSuffixToMakeValidStringIncomplete = Some(genNonEmptyString.suchThat(_.head != 'x')),
      genInvalidStrings = None
    )
  }
}
