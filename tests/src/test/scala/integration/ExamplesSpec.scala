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

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class ExamplesSpec extends FreeSpec with Matchers with ScalaCheckDrivenPropertyChecks {
  "Some examples of valid ropes include" - {
    "twitter handles" - {
      //This is very simplified - starts with an '@', then upper or lowercase letter characters
      type Username      = Repeated[1, 15, Letter]
      type TwitterHandle = Literal['@'] +: Username
      "parsing and de-composing" in {
        val Right(parsed) = Rope.parseTo[TwitterHandle]("@HowyP")
        parsed.suffix.write should be("HowyP")
      }
      "composing and writing" in {
        val handle: TwitterHandle = '@' +: Rope.parseTo[Username]("HowyP").getOrElse(fail())
        handle.write should be("@HowyP")
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
      type PostCode = Concat[PostCode.OutwardCode, Concat[Literal[' '], PostCode.InwardCode]]
      object PostCode {
        type Area     = Repeated[1, 2, Letter.Uppercase]
        type District = Concat[Repeated[1, 2, Digit] ConvertedTo Int, Optional[Letter.Uppercase]]
        type OutwardCode =
          Concat[Area Named "Area", District Named "District"] Or (Literal['G'] +: Literal['I'] +: Literal['R'])

        type Sector     = Digit
        type Unit       = Repeated[2, 2, Letter.Uppercase]
        type InwardCode = Sector +: Unit
      }
      "parsing and de-composing" - {
        "CR2 6XH" in {
          val Right(postCode) = Rope.parseTo[PostCode]("CR2 6XH")

          val Or.First(outward) = postCode.section[1]
          outward.section["Area"].values.map(_.value) should be(List('C', 'R'))
          outward.section["District"].prefix.value should be(2)
          outward.write should be("CR2")

          val sector = postCode.section[3]
          sector.value should be(6)

          postCode.section[4].values.map(_.value) should be(List('X', 'H'))
        }
        "M1 1AE" in {
          val Right(Or.First(outward) +: _) = Rope.parseTo[PostCode]("M1 1AE")
          outward.section["Area"].values.map(_.value) should contain only 'M'
          outward.section["District"].suffix.value should be(None)
        }
        "DN55 1PT" in {
          val Right(Or.First(outward) +: _) = Rope.parseTo[PostCode]("DN55 1PT")
          outward.section["Area"].values.map(_.value) should be(List('D', 'N'))
          outward.section["District"].prefix.value should be(55)
          outward.section["District"].suffix.value should be(None)
        }
        "EC1A 1BB" in {
          val Right(Or.First(outward) +: _) = Rope.parseTo[PostCode]("EC1A 1BB")
          outward.section["District"].prefix.value should be(1)
          outward.section["District"].suffix.value.get.value should be('A')
        }
        "GIR 0AA" in {
          val Right(Or.Second(outward) +: _) = Rope.parseTo[PostCode]("GIR 0AA")
          outward.write should be("GIR")
        }
      }
      "composing and writing" - {
        "CR2 6XH" in {
//          TODO Can we do this better? For instance allow the ' ' char to be implicit
//          TODO should we change the variance of rope subclasses to avoid the explicity typing for Optional?
          val postcode = for {
            area                          <- Rope.parseTo[PostCode.Area]("CR")
            district                      <- Rope.parseTo[PostCode.District]("2")
            outward: PostCode.OutwardCode = Or.First(Named(area, "Area") +: Named(district, "District"))
            sector                        <- Digit.from(6)
            unit                          <- Rope.parseTo[PostCode.Unit]("XH")
          } yield outward +: ' ' +: sector +: unit
          postcode.getOrElse(fail()).write should be("CR2 6XH")
        }
        "EC1A 1BB" in {
          val postcode = for {
            area                          <- Rope.parseTo[PostCode.Area]("EC")
            district                      <- Rope.parseTo[PostCode.District]("1A")
            outward: PostCode.OutwardCode = Or.First(Named(area, "Area") +: Named(district, "District"))
            inward                        <- Rope.parseTo[PostCode.InwardCode]("1BB")
          } yield outward +: ' ' +: inward
          postcode.getOrElse(fail()).write should be("EC1A 1BB")
        }
      }
      "generating" in {
        forAll { postcode: PostCode =>
          Rope.parseTo[PostCode](postcode.write) should be(a[Right[_, _]])
        }
      }
    }
    "UK National Insurance Numbers" - {
      // See https://en.wikipedia.org/wiki/National_Insurance_number
      // "The format of the number is two prefix letters, six digits, and one suffix letter.
      // The suffix letter is either A, B, C, or D"
      type NINO =
        Repeated.Exactly[2, Letter.Uppercase] +: (Repeated.Exactly[6, Digit] ConvertedTo Int) +: Range['A', 'D']
      "QQ123456C" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[NINO]("QQ123456C").getOrElse(fail())
          parsed.section[1].write should be("QQ")
          parsed.section[2].value should be(123456)
          parsed.section[3].value should be('C')
        }
      }
      "AA000000A" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[NINO]("AA000000A").getOrElse(fail())
          parsed.section[1].write should be("AA")
          parsed.section[2].value should be(0)
          parsed.section[3].value should be('A')
        }
      }
      "generating" in {
        forAll { nino: NINO =>
          Rope.parseTo[NINO](nino.write) should be(a[Right[_, _]])
        }
      }
      "Invalid NINOs fail to parse" in {
        val l = List(
          "Q0123456C", // Prefix replaced with number
          "Q123456C", // Prefix of single letter
          "QQ 12 34 56 C", // Spaces added
          "QQ123456E" // Out of scope letter for suffix
        )
        every(l.map(Rope.parseTo[NINO](_))) should be(Left(Rope.InvalidValue))
      }
    }
    "Social Security Number" - {
      // https://en.wikipedia.org/wiki/Social_Security_number#Structure
      // The Social Security number is a nine-digit number in the format "AAA-GG-SSSS".
      // The number is divided into three parts: the first three digits, known as the area
      // number because they were formerly assigned by geographical region; the middle two
      // digits, known as the group number; and the final four digits, known as the
      // serial number.
      type Area   = Repeated.Exactly[3, Digit] ConvertedTo Int Named "Area"
      type Group  = Repeated.Exactly[2, Digit] ConvertedTo Int Named "Group"
      type Serial = Repeated.Exactly[4, Digit] ConvertedTo Int Named "Serial"
      type Dash   = Literal['-']
      type SSN    = Area +: Dash +: Group +: Dash +: Serial
      "078-05-1120" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[SSN]("078-05-1120").getOrElse(fail())
          parsed.section["Area"].value should be(78)
          parsed.section["Group"].value should be(5)
          parsed.section["Serial"].value should be(1120)
        }
      }
    }
  }
}
