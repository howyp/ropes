package unit

import org.scalacheck.Arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._
import ropes.scalacheck._

class AnyStringSpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "An `AnyString` Rope" - {
    "Always parses to complete for any string" in forAll { s: String =>
      Rope.parseTo[AnyString](s) should be(Parse.Result.Complete(AnyString(s)))
    }
    "Always writes back to the same string" in forAll { s: String =>
      AnyString(s).write should be(s)
    }
    "Round-trips valid strings by parsing and writing back to an identical string" in forAll(
      Arbitrary.arbitrary[String]) { original =>
      val Parse.Result.Complete(parsed) = Rope.parseTo[AnyString](original)
      parsed.write should be(original)
    }
    "Round-trips arbitrary values by writing and parsing back to an identical value" in forAll(
      Arbitrary.arbitrary[AnyString]) { original =>
      val Parse.Result.Complete(parsed) = Rope.parseTo[AnyString](original.write)
      parsed should be(original)
    }
  }
}
