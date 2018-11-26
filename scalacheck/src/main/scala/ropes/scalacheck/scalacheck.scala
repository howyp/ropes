package ropes

import org.scalacheck.{Arbitrary, Gen}

package object scalacheck {
  implicit def arbExactlyChar[C <: Char with Singleton](implicit c: ValueOf[C]): Arbitrary[Exactly[C]] =
    Arbitrary(Gen.const(Exactly(c.value)))

  implicit val arbAnyString: Arbitrary[AnyString] = Arbitrary(Arbitrary.arbitrary[String].map(AnyString.apply))

  implicit def arbConcat[P <: Rope: Arbitrary, S <: Rope: Arbitrary]: Arbitrary[Concat[P, S]] =
    Arbitrary(Gen.resultOf(Concat.apply[P, S] _))

  implicit def arbRange[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Arbitrary[Range[Start, End]] =
    Arbitrary(Gen.choose(start.value: Char, end.value: Char).map(Range.unsafeFrom[Start, End]))

  implicit def arbConvertedTo[Source <: Rope, Target](
      implicit arbSource: Arbitrary[Source],
      conversion: Conversion[Source, Target]): Arbitrary[ConvertedTo[Source, Target]] =
    Arbitrary(arbSource.arbitrary.map(conversion.forwards).map(ConvertedTo.apply[Source, Target]))
}
