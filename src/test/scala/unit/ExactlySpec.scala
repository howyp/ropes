package unit

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes._

class ExactlySpec extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "An `Exactly[_] Rope" - {
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
//    Exactly('a').write should be("a")
//    Rope.generateArbitrary[Exactly['a']].toList should contain only Exactly('a')
    }
  }
}
