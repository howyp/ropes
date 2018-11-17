package ropes

import org.scalacheck.{Arbitrary, Gen}

package object scalacheck {
  implicit def arbExactlyChar[C <: Char with Singleton](implicit c: ValueOf[C]): Arbitrary[Exactly[C]] =
    Arbitrary(Gen.const(Exactly(c.value)))

  implicit val arbAnyString: Arbitrary[AnyString] = Arbitrary(Arbitrary.arbitrary[String].map(AnyString.apply))

  implicit def arbConcat[P <: Rope: Arbitrary, S <: Rope: Arbitrary]: Arbitrary[Concat[P, S]] =
    Arbitrary(Gen.resultOf(Concat.apply[P, S] _))
}
