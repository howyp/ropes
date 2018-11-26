package unit
import gens.CommonGens
import laws.RopeLaws
import org.scalacheck.Gen
import ropes._
import ropes.scalacheck._

class DigitSpec extends RopeLaws with CommonGens {
  "A Digit" - {
    `obeys Rope laws`[Digit](
      genValidStringsWithDecompositionAssertion = Gen.choose(0, 9).map { digit =>
        digit.toString -> (_.value should be(digit))
      },
      genSuffixToValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings = Some(
        Gen
          .oneOf(
            Gen.choose(Char.MinValue, ('0' - 1).toChar),
            Gen.choose(('9' + 1).toChar, Char.MaxValue)
          )
          .map(_.toString)
      )
    )
  }
}
