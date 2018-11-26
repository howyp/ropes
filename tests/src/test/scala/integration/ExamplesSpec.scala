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
        type District    = Range['1', '9']
        type OutwardCode = Area :+ District

        type Sector     = Range['1', '9']
        type Unit       = Range['A', 'Z'] :+ Range['A', 'Z']
        type InwardCode = Sector :+ Unit
      }
      "parsing and de-composing" in {
        val Parse.Result.Complete(outward :+ _ :+ inward) = Rope.parseTo[PostCode]("CR2 6XH")

        outward.prefix.prefix.value should be('C')
        outward.prefix.suffix.value should be('R')
        outward.suffix.value should be('2')
        outward.write should be("CR2")

        inward.prefix.value should be('6')
        inward.suffix.prefix.value should be('X')
        inward.suffix.suffix.value should be('H')
        inward.write should be("6XH")
      }
    }
  }
}
