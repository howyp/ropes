package unit

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import ropes.{Parse, Rope, Write}

trait RopeLaws extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  def `obeys Rope laws`[R <: Rope: Parse: Arbitrary: Write](
      genValidStrings: Gen[String]
  ): Unit = {
    "Round-trips valid strings by parsing and writing back to an identical string" in forAll(genValidStrings) {
      original =>
        val Parse.Result.Complete(parsed) = Rope.parseTo[R](original)
        parsed.write should be(original)
    }
    "Round-trips arbitrary values by writing and parsing back to an identical value" in forAll(Arbitrary.arbitrary[R]) {
      original =>
        val Parse.Result.Complete(parsed) = Rope.parseTo[R](original.write)
        parsed should be(original)
    }
  }
}
