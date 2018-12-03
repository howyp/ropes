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

import ropes.core._
import ropes.dsl._
import ropes.scalacheck._

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class ExamplesSpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "Some examples of valid ropes include" - {
    "twitter handles" - {
      //This is very simplified - starts with an '@', then any characters
      type TwitterHandle = Exactly['@'] +: AnyString
      "parsing and de-composing" in {
        val Right(parsed) = Rope.parseTo[TwitterHandle]("@howyp")
        parsed.suffix.value should be("howyp")
      }
      "composing and writing" in {
        val handle: TwitterHandle = '@' +: AnyString("howyp")
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
      type PostCode = Concat[PostCode.OutwardCode, Concat[Exactly[' '], PostCode.InwardCode]]
      object PostCode {
        type Area        = Concat[Range['A', 'Z'], Optional[Range['A', 'Z']]]
        type District    = Concat[OneOrTwoDigits, Optional[Range['A', 'Z']]]
        type OutwardCode = Concat[Area, District]

        type Sector     = Digit
        type Unit       = Range['A', 'Z'] +: Range['A', 'Z']
        type InwardCode = Sector +: Unit
      }
      "parsing and de-composing" - {
        "CR2 6XH" in {
          val Right(postCode) = Rope.parseTo[PostCode]("CR2 6XH")

          val outward = postCode.section[1]
          outward.section[1].section[1].value should be('C')
          outward.section[1].section[2].value.get.value should be('R')
          outward.section[2].value should be(2)
          outward.write should be("CR2")

          val sector = postCode.section[3]
          sector.value should be(6)

          postCode.section[4].value should be('X')
          postCode.section[5].value should be('H')
        }
        "M1 1AE" in {
          val Right((area +: district) +: _) = Rope.parseTo[PostCode]("M1 1AE")
          area.prefix.value should be('M')
          district.suffix.value should be(None)
        }
        "DN55 1PT" in {
          val Right(outward +: _) = Rope.parseTo[PostCode]("DN55 1PT")
          outward.prefix.prefix.value should be('D')
          outward.prefix.suffix.value.get.value should be('N')
          outward.suffix.prefix.value should be(55)
          outward.suffix.suffix.value should be(None)
        }
        "EC1A 1BB" in {
          val Right(outward +: _) = Rope.parseTo[PostCode]("EC1A 1BB")
          outward.suffix.prefix.value should be(1)
          outward.suffix.suffix.value.get.value should be('A')
        }
      }
      "composing and writing" - {
        "CR2 6XH" in {
//          TODO Can we do this better? For instance allow the ' ' char to be implicit
//          TODO should we change the variance of rope subclasses to avoid the explicity typing for Optional?
          val Right(postcode: PostCode) = for {
            area     <- Rope.parseTo[PostCode.Area]("CR")
            district <- OneOrTwoDigits.from(2).map(_ +: Optional[Range['A', 'Z']](None))
            sector   <- Digit.from(6)
            unit     <- Rope.parseTo[PostCode.Unit]("XH")
          } yield (area +: district) +: ' ' +: sector +: unit
          postcode.write should be("CR2 6XH")
        }
        "EC1A 1BB" in {
          val Right(postcode: PostCode) = for {
            area     <- Rope.parseTo[PostCode.Area]("EC")
            district <- OneOrTwoDigits.from(1).map(_ +: Optional(Range.from['A', 'Z']('A').toOption))
            inward   <- Rope.parseTo[PostCode.InwardCode]("1BB")
          } yield (area +: district) +: ' ' +: inward
          postcode.write should be("EC1A 1BB")
        }
      }
      "generating" in {
        forAll { postcode: PostCode =>
          Rope.parseTo[PostCode](postcode.write) should be('right)
        }
      }
    }
  }
}
