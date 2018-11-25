package unit

import laws.RopeLaws
import org.scalacheck.Gen

import ropes._
import ropes.scalacheck._

class RangeSpec extends RopeLaws {
  "A Range['a','z']" - {
    `obeys Rope laws`[Range['a', 'z']](
      Gen.choose('a', 'z').map { char =>
        char.toString -> (_.value should be(char))
      },
      None,
      None
    )
  }
}
