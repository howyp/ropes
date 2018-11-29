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

package laws
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes.{Parse, Rope, Write}

trait RopeLaws extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  private implicit def noShrink[T]: Shrink[T] = Shrink(_ => Stream.empty[T])

  def `obeys Rope laws`[R <: Rope: Parse: Arbitrary: Write](
      genValidStringsWithDecompositionAssertion: Gen[(String, R => Unit)],
      genSuffixToMakeValidStringIncomplete: Option[Gen[String]],
      genInvalidStrings: Option[Gen[String]]
  ): Unit = {
    val genValidStrings = genValidStringsWithDecompositionAssertion.map(_._1)
    "Parses correctly when complete" in forAll(genValidStringsWithDecompositionAssertion, minSuccessful(10000)) {
      case (str, assertion) =>
        val Parse.Result.Complete(parsed) = Parse[R].parse(str)
        assertion(parsed)
    }
    genSuffixToMakeValidStringIncomplete.foreach { genSuffixToValidStringIncomplete =>
      "Parses correctly when incomplete" in forAll(genValidStringsWithDecompositionAssertion,
                                                   genSuffixToValidStringIncomplete,
                                                   minSuccessful(10000)) { (strAndAssertion, suffix) =>
        val (str, assertion)                           = strAndAssertion
        val Parse.Result.Incomplete(parsed, remaining) = Parse[R].parse(str + suffix)
        assertion(parsed)
        remaining should be(suffix)
      }
    }
    genInvalidStrings.foreach { genInvalidStrings =>
      "Fails to parse when invalid" in forAll(genInvalidStrings, minSuccessful(10000)) { str =>
        Parse[R].parse(str) should be(Parse.Result.Failure)
      }
    }
    "Round-trips valid strings by parsing and writing back to an identical string" in forAll(genValidStrings,
                                                                                             minSuccessful(10000)) {
      original =>
        val result = Parse[R].parse(original)
        result should be(a[Parse.Result.Complete[_]])
        val Parse.Result.Complete(parsed) = result
        Write[R].write(parsed) should be(original)
    }
    "Round-trips arbitrary values by writing and parsing back to an identical value" in forAll(minSuccessful(10000)) {
      original: R =>
        val written = Write[R].write(original)
        withClue(s"Wrote '$written'") {
          Parse[R].parse(written) should be(Parse.Result.Complete(original))
        }
    }
  }
}
