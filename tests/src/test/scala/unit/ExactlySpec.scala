package unit

import gens.CommonGens
import laws.RopeLaws
import org.scalacheck.{Arbitrary, Gen}
import ropes._
import ropes.scalacheck._

import scala.Some

class ExactlySpec extends RopeLaws with CommonGens {
  "An `Exactly[_]` Rope" - {
    "accepts literal chars" - {
      `obeys Rope laws`[Exactly['a']](
        genValidStringsWithDecompositionAssertion = Gen.const("a").map { str =>
          str -> { parsed =>
            parsed.value should be('a')
          }
        },
        genSuffixToValidStringIncomplete = Some(genNonEmptyString),
        genInvalidStrings = Some(Gen.oneOf(Gen.const(""), genNonEmptyString.suchThat(_.head != 'a')))
      )
      "Can be parsed when incomplete" in forAll { suffix: String =>
        whenever(suffix.nonEmpty) {
          Rope.parseTo[Exactly['a']]("a" + suffix) should be(Parse.Result.Success(Exactly('a'), suffix))
        }
      }
      "Captures the literal type when using .apply(...)" in {
        val a: Exactly['a'] = Exactly('a')
        """val b: Exactly['a'] = Exactly('b')""" shouldNot typeCheck
      }
    }
    "does not accept non-singletons" in {
      """Exactly[Char]('a')""" shouldNot compile
    }
  }
  "does not accept non-chars (for the moment)" in {
    """Exactly["a"]("a")""" shouldNot compile
    """Exactly[1](1)""" shouldNot compile
  }
}
