package gens
import org.scalacheck.Gen

trait CommonGens {
  val genNonEmptyString: Gen[String] = Gen.resultOf((c: Char, s: String) => c + s)
}
