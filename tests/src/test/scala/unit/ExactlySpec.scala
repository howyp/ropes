package unit

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
        genSuffixToValidStringIncomplete = Some(genNonEmptyString)
      )
      "Can be parsed when incomplete" in forAll { suffix: String =>
        whenever(suffix.nonEmpty) {
          Rope.parseTo[Exactly['a']]("a" + suffix) should be(Parse.Result.Incomplete(Exactly('a'), suffix))
        }
      }
      "Cannot be parsed with an empty string" in (
        Rope.parseTo[Exactly['a']]("") should be(Parse.Result.Failure)
      )
      "Cannot be parsed with the wrong character" in forAll { (prefix: Char, suffix: String) =>
        whenever(prefix != 'a') {
          Rope.parseTo[Exactly['a']](prefix + suffix) should be(Parse.Result.Failure)
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
