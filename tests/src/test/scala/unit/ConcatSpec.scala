package unit

import org.scalacheck.{Arbitrary, Gen}
import ropes._
import ropes.scalacheck._

class ConcatSpec extends RopeLaws {
  "A `Concat` Rope" - {
    "with an exact prefix and suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], Exactly['b']]](
        genValidStringsWithDecompositionAssertion = Gen.const {
          "ab" -> { parsed =>
            parsed.prefix should be(Exactly('a'))
            parsed.suffix should be(Exactly('b'))
          }
        }
      )
    }
    "with an exact prefix an AnyString suffix" - {
      `obeys Rope laws`[Concat[Exactly['a'], AnyString]](
        genValidStringsWithDecompositionAssertion = Arbitrary.arbitrary[String].map { suffix =>
          "a" + suffix -> { parsed =>
            parsed.prefix should be(Exactly('a'))
            parsed.suffix should be(AnyString(suffix))
          }
        }
      )
    }
  }
}
