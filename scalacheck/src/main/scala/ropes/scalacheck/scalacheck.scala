package ropes

import org.scalacheck.{Arbitrary, Gen}

package object scalacheck {
  implicit def arbExactlyChar[C <: Char with Singleton](implicit c: ValueOf[C]): Arbitrary[Exactly[C]] =
    Arbitrary(Gen.const(Exactly(c.value)))
}
