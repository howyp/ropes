package unit

import org.scalacheck.Gen
import ropes._
import ropes.scalacheck._

class ExactlySpec extends RopeLaws {
  "An `Exactly[_]` Rope" - {
    "accepts literal chars" - {
      `obeys Rope laws`[Exactly['a']](Gen.const("a"))
      "Can be parsed when complete" in (
        Rope.parseTo[Exactly['a']]("a") should be(Parse.Result.Complete(Exactly('a')))
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
