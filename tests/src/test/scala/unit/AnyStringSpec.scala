package unit

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._

class AnyStringSpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "An `AnyString` Rope" - {
    "Always parses to complete for any string" in forAll { s: String =>
      Rope.parseTo[AnyString](s) should be(Parse.Result.Complete(AnyString(s)))
    }
    "Always writes back to the same string" in forAll { s: String =>
      AnyString(s).write should be(s)
    }
    //TODO Generate instance for String
  }
}
