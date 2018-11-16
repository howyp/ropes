package unit

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._

class ExactlySpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "An `Exactly[_]` Rope" - {
    "accepts literal chars" - {
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
      "Writing returns the same char" in forAll { char: Char =>
        Exactly(char).write should be(s"$char")
      }
      "Generating returns only the char" in {
        pending
        //TODO re-enable as part of #5
        //Rope.generateArbitrary[Exactly['a']].toList should contain only Exactly('a')
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
