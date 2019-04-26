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

import laws.RopeLaws
import org.scalacheck.Arbitrary
import ropes.core._
import ropes.scalacheck._

class AnyStringSpec extends RopeLaws {
  "An `AnyString` Rope" - {
    "Always parses to complete for any string" in forAll { s: String =>
      Parse[AnyString Named "Example"].parse(s) should be(Parse.Result.Complete(AnyString(s)))
      Parse[AnyString].parse(s) should be(Parse.Result.Complete(AnyString(s)))
      Parse[AnyString].parse(s) should be(Parse.Result.Complete(AnyString(s).assignName["Example"]))
    }
    `obeys Rope laws`[AnyString](
      genValidStringsWithDecompositionAssertion = Arbitrary.arbitrary[String].map { str =>
        str -> { parsed =>
          parsed.value should be(str)
        }
      },
      genSuffixToMakeValidStringIncomplete = None,
      genInvalidStrings = None
    )
  }
}
