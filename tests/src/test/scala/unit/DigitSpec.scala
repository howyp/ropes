package unit
import laws.RopeLaws
import org.scalacheck.Gen
import ropes._
import ropes.scalacheck._

class DigitSpec extends RopeLaws {
  "A Digit" - {
    `obeys Rope laws`[Digit](
      genValidStringsWithDecompositionAssertion = Gen.choose(0, 9).map { digit =>
        digit.toString -> (_.value should be(digit))
      },
      genSuffixToValidStringIncomplete = None,
      genInvalidStrings = None
    )
  }
}
