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
import ropes.core.Spec._
import ropes.dsl._
import ropes.scalacheck._

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class ExamplesSpec extends FreeSpec with Matchers with ScalaCheckDrivenPropertyChecks {
  "Some examples of valid ropes include" - {
    "twitter handles" - {
      //From https://help.twitter.com/en/managing-your-account/twitter-username-rules :
      //Username cannot be longer than 15 characters. Your name can be longer (50 characters), but usernames are kept
      // shorter for the sake of ease. A username can only contain alphanumeric characters (letters A-Z, numbers 0-9)
      // with the exception of underscores
      type Username      = Repeated[1, 15, CharacterClass[('a' - 'z') || ('A' - 'Z') || ('0' - '9') || ==['_']]]
      type TwitterHandle = Literal['@'] +: Username
      "parsing and de-composing" in {
        val Right(parsed) = Rope.parseTo[TwitterHandle]("@HowyP_1")
        parsed.suffix.write should be("HowyP_1")
      }
      "validating" in {
        Rope.parseTo[TwitterHandle]("HowyP") should be(Left(Rope.InvalidValue))             // No @ prefix
        Rope.parseTo[TwitterHandle]("@") should be(Left(Rope.InvalidValue))                 // No suffix
        Rope.parseTo[TwitterHandle]("@abcdefghijklmnop") should be(Left(Rope.InvalidValue)) // Suffix too long
        Rope.parseTo[TwitterHandle]("@HowyP*") should be(Left(Rope.InvalidValue))           // No @ prefix
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
        type Area        = Repeated[1, 2, Letter.Uppercase] Named "Area"
        type District    = Concat[Repeated[1, 2, Digit] ConvertedTo Int, Optional[Letter.Uppercase]] Named "District"
        type OutwardCode = Concat[Area, District] Or (Literal['G'] +: Literal['I'] +: Literal['R'])

        type Sector     = Digit Named "Sector"
        type Unit       = Repeated[2, 2, Letter.Uppercase] Named "Unit"
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
          val postcode = for {
            area                          <- Rope.parseTo[PostCode.Area]("CR")
            district                      <- Rope.parseTo[PostCode.District]("2")
            outward: PostCode.OutwardCode = Or.First(area +: district)
            sector                        <- Digit.from(6)
            unit                          <- Rope.parseTo[PostCode.Unit]("XH")
          } yield outward +: ' ' +: sector +: unit
          postcode.getOrElse(fail()).write should be("CR2 6XH")
        }
        "EC1A 1BB" in {
          val postcode = for {
            area                          <- Rope.parseTo[PostCode.Area]("EC")
            district                      <- Rope.parseTo[PostCode.District]("1A")
            outward: PostCode.OutwardCode = Or.First(area +: district)
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
        "composing and writing" in pending //TODO
      }
      "AA000000A" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[NINO]("AA000000A").getOrElse(fail())
          parsed.section[1].write should be("AA")
          parsed.section[2].value should be(0)
          parsed.section[3].value should be('A')
        }
        "composing and writing" in pending //TODO
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
      //TODO don't require literals to be in the case class
      //TODO recursive conversions, such that Area, Group, Serial can be `Int` in the case class
      case class SocialSecurityNumber(Area: Area, dash1: Dash, Group: Group, dash2: Dash, Serial: Serial)

      type Area   = Repeated.Exactly[3, Digit] ConvertedTo Int Named "Area"
      type Group  = Repeated.Exactly[2, Digit] ConvertedTo Int Named "Group"
      type Serial = Repeated.Exactly[4, Digit] ConvertedTo Int Named "Serial"
      type Dash   = Literal['-']
      type SSN    = Area +: Dash +: Group +: Dash +: Serial

      import ropes.generic._

      "078-05-1120" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[SSN ConvertedTo SocialSecurityNumber]("078-05-1120").getOrElse(fail())
          parsed.value.Area.value should be(78)
          parsed.value.Group.value should be(5)
          parsed.value.Serial.value should be(1120)
          parsed.write should be("078-05-1120")
        }
        "composing and writing" in {
          //TODO can we make this more elegant, only supplying the Rope type to `fromTarget`?
          val Right(composed: SSN) = for {
            area   <- ConvertedTo.fromTarget[Repeated.Exactly[3, Digit], Int](78)
            group  <- ConvertedTo.fromTarget[Repeated.Exactly[2, Digit], Int](5)
            serial <- ConvertedTo.fromTarget[Repeated.Exactly[4, Digit], Int](1120)
          } yield area.assignName["Area"] +: '-' +: group.assignName["Group"] +: '-' +: serial.assignName["Serial"]

          composed.write should be("078-05-1120")
        }
        "composing and writing via the conversion" in {
          //TODO can we make this more elegant, only supplying the Rope type to `fromTarget`?
          //TODO re-enable
          val Right(composed) =
            for {
              area   <- ConvertedTo.fromTarget[Repeated.Exactly[3, Digit], Int](78)
              group  <- ConvertedTo.fromTarget[Repeated.Exactly[2, Digit], Int](5)
              serial <- ConvertedTo.fromTarget[Repeated.Exactly[4, Digit], Int](1120)
            } yield
              SocialSecurityNumber(area.assignName["Area"],
                                   Literal['-'],
                                   group.assignName["Group"],
                                   Literal['-'],
                                   serial.assignName["Serial"])
          ConvertedTo
            .fromTarget[SSN, SocialSecurityNumber](composed)
            .right
            .get
            .write should be("078-05-1120")
        }
      }
      "generating" in {
        forAll { ssn: SSN =>
          Rope.parseTo[SSN](ssn.write) should be(a[Right[_, _]])
        }
      }
    }
    "Hostnames" - {
      type Domain   = Repeated[1, 63, CharacterClass[('a' - 'z') || ('A' - 'Z') || ('0' - '9') || ==['-']]]
      type Hostname = Repeated[0, 10, Domain +: Literal['.']] +: (Domain Named "TLD")
      "localhost" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[Hostname]("localhost").getOrElse(fail())
          parsed.section[1].values should be(empty)
          parsed.section["TLD"].write should be("localhost")
        }
      }
      "www.google.com" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[Hostname]("www.google.com").getOrElse(fail())
          parsed.section[1].values.map(_.section[1].write) should contain inOrderOnly ("www", "google")
          parsed.section["TLD"].write should be("com")
        }
      }
      "hyphens-and-digits-5.com" - {
        "parsing and de-composing" in {
          val parsed = Rope.parseTo[Hostname]("hyphens-and-digits-5.com").getOrElse(fail())
          parsed
            .section[1]
            .values
            .map(_.section[1].write) should contain only "hyphens-and-digits-5"
          parsed.section["TLD"].write should be("com")
        }
      }
    }
  }
}
