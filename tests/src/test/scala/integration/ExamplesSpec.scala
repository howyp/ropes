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
      type OutwardCode = Range['A', 'Z'] :+ Range['A', 'Z'] :+ Range['1', '9']
      type InwardCode  = Range['1', '9'] :+ Range['A', 'Z'] :+ Range['A', 'Z']
      type PostCode    = OutwardCode :+ Exactly[' '] :+ InwardCode

      "parsing and de-composing" in {
        val Parse.Result.Complete(parsed) = Rope.parseTo[PostCode]("CR2 6XH")
        parsed.prefix.prefix.prefix.prefix.value should be('C')
        parsed.prefix.prefix.prefix.suffix.value should be('R')
        parsed.prefix.prefix.suffix.value should be('2')
        parsed.prefix.prefix.write should be("CR2")

        parsed.prefix.suffix.value should be(' ')

        parsed.suffix.prefix.prefix.value should be('6')
        parsed.suffix.prefix.suffix.value should be('X')
        parsed.suffix.suffix.value should be('H')
        parsed.suffix.write should be("6XH")
      }
    }
  }
}
