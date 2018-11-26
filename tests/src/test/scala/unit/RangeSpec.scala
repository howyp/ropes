package unit

import gens.CommonGens
import laws.RopeLaws
import org.scalacheck.Gen
import ropes._
import ropes.scalacheck._

class RangeSpec extends RopeLaws with CommonGens {
  "A Range['a','z']" - {
    val genAtoZ = Gen.choose('a', 'z')
    val genNonAtoZ = Gen
      .oneOf(
        Gen.choose(Char.MinValue, ('a' - 1).toChar),
        Gen.choose(('z' + 1).toChar, Char.MaxValue)
      )

    `obeys Rope laws`[Range['a', 'z']](
      genValidStringsWithDecompositionAssertion = genAtoZ.map { char =>
        char.toString -> (_.value should be(char))
      },
      genSuffixToValidStringIncomplete = Some(genNonEmptyString),
      genInvalidStrings = Some(genNonAtoZ.map(_.toString))
    )
    "Range can be created from valid characters" - forAll(genAtoZ) { char =>
      Range.from['a', 'z'](char).get should have('value (char))
    }
    "Range cannot be created from invalid characters" - forAll(genNonAtoZ) { char =>
      Range.from['a', 'z'](char) should be(empty)
    }
  }
}
