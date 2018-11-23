package integration

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._
import ropes.dsl._
import ropes.scalacheck._

class ExamplesSpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "Some examples of valid ropes include" - {
    //This is very simplified - starts with an '@', then any characters
    type TwitterHandle = Exactly['@'] :+ AnyString
    "twitter handles" - {
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
  }
}
