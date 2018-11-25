package unit

import gens.CommonGens
import laws.RopeLaws
import org.scalacheck.Gen
import ropes._
import ropes.scalacheck._

class RangeSpec extends RopeLaws with CommonGens {
  "A Range['a','z']" - {
    `obeys Rope laws`[Range['a', 'z']](
      genValidStringsWithDecompositionAssertion = Gen.choose('a', 'z').map { char =>
        char.toString -> (_.value should be(char))
      },
      genSuffixToValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings = Some(
        Gen
          .oneOf(
            Gen.choose(Char.MinValue, ('a' - 1).toChar),
            Gen.choose(('a' + 1).toChar, Char.MaxValue)
          )
          .map(_.toString)
      )
    )
  }
}
