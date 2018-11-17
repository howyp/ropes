package unit

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes.{Parse, Rope, Write}

trait RopeLaws extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  def `obeys Rope laws`[R <: Rope: Parse: Arbitrary: Write](
      genValidStringsWithDecompositionAssertion: Gen[(String, R => Unit)]
  ): Unit = {
    val genValidStrings = genValidStringsWithDecompositionAssertion.map(_._1)
    "Parses correctly" in forAll(genValidStringsWithDecompositionAssertion) {
      case (str, assertion) =>
        val Parse.Result.Complete(parsed) = Rope.parseTo[R](str)
        assertion(parsed)
    }
    "Round-trips valid strings by parsing and writing back to an identical string" in forAll(genValidStrings) {
      original =>
        val Parse.Result.Complete(parsed) = Rope.parseTo[R](original)
        parsed.write should be(original)
    }
    "Round-trips arbitrary values by writing and parsing back to an identical value" in forAll(Arbitrary.arbitrary[R]) {
      original =>
        val written = original.write
        withClue(s"Wrote '$written'") {
          Rope.parseTo[R](written) should be(Parse.Result.Complete(original))
        }
    }
  }
}
