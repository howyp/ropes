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

package integration

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._
import ropes.dsl._
import ropes.scalacheck._

class ExamplesSpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "Some examples of valid ropes include" - {
    "twitter handles" - {
      //This is very simplified - starts with an '@', then any characters
      type TwitterHandle = Exactly['@'] :+ AnyString
      "parsing and de-composing" in {
        val Parse.Result.Complete(parsed) = Rope.parseTo[TwitterHandle]("@howyp")
        parsed.suffix.value should be("howyp")
      }
      "composing and writing" in {
        val handle: TwitterHandle = '@' :+ AnyString("howyp")
        handle.write should be("@howyp")
      }
      "generating" in {
        forAll { handle: TwitterHandle =>
          handle.write should startWith("@")
        }
      }
    }
    "UK Postcodes" - {
      //Wikipedia lists a validation Regex as:
      // ^([A-Za-z][A-Ha-hJ-Yj-y]?[0-9][A-Za-z0-9]? [0-9][A-Za-z]{2}|[Gg][Ii][Rr] 0[Aa]{2})$
      type PostCode = PostCode.OutwardCode :+ Exactly[' '] :+ PostCode.InwardCode
      object PostCode {
        type Area        = Range['A', 'Z'] :+ Range['A', 'Z']
        type District    = Digit
        type OutwardCode = Area :+ District

        type Sector     = Digit
        type Unit       = Range['A', 'Z'] :+ Range['A', 'Z']
        type InwardCode = Sector :+ Unit
      }
      "parsing and de-composing" in {
        val Parse.Result.Complete(outward :+ _ :+ inward) = Rope.parseTo[PostCode]("CR2 6XH")

        outward.prefix.prefix.value should be('C')
        outward.prefix.suffix.value should be('R')
        outward.suffix.value should be(2)
        outward.write should be("CR2")

        inward.prefix.value should be(6)
        inward.suffix.prefix.value should be('X')
        inward.suffix.suffix.value should be('H')
        inward.write should be("6XH")
      }
      "composing and writing" in {
        //TODO Can we do this better? For instance allow the ' ' char to be implicit
        val Parse.Result.Complete(outward) = Rope.parseTo[PostCode.OutwardCode]("CR2")
        val Parse.Result.Complete(inward)  = Rope.parseTo[PostCode.InwardCode]("6XH")
        val postcode: PostCode             = outward :+ Exactly(' ') :+ inward
        postcode.write should be("CR2 6XH")
      }
      "generating" in {
        forAll { postcode: PostCode =>
          Rope.parseTo[PostCode](postcode.write) should be(a[Parse.Result.Complete[_]])
        }
      }
    }
  }
}
