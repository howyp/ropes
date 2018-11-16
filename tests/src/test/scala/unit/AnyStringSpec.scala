package unit

import org.scalacheck.Arbitrary
import ropes._
import ropes.scalacheck._

class AnyStringSpec extends RopeLaws {
  "An `AnyString` Rope" - {
    "Always parses to complete for any string" in forAll { s: String =>
      Rope.parseTo[AnyString](s) should be(Parse.Result.Complete(AnyString(s)))
    }
    `obeys Rope laws`[AnyString](
      genValidStrings = Arbitrary.arbitrary[String]
    )
  }
}
