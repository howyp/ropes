/*
 * Copyright 2018 Howard Perrin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unit
import org.scalacheck.{Gen, Shrink}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ropes.core._
import ropes.scalacheck._

class RopeCompanionSpec extends FreeSpec with Matchers with ScalaCheckDrivenPropertyChecks {
  private implicit def noShrink[T]: Shrink[T] = Shrink(_ => Stream.empty[T])

  "The Rope companion object" - {
    "Can parse to a given rope" - {
      "Successfully if the parse is complete" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Success(AnyString(""), "")
        Rope.parseTo[AnyString]("") should be(Right(AnyString("")))
      }
      "Failing if the parse is incomplete" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Success(AnyString(""), "remaining")
        Rope.parseTo[AnyString]("") should be(Left(Rope.InvalidValue))
      }
      "Failing if the parse is a failure" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Failure
        Rope.parseTo[AnyString]("") should be(Left(Rope.InvalidValue))
      }
    }
  }
  "A built companion for the specific rope type" - {
    "Literal" - {
      "just contains the value itself" in {
        type A = Literal['a']
        val A = RopeCompanion[A]
        A.value should be('a')
      }
    }
    "AnyString" - {
      type Example = AnyString
      val Example = RopeCompanion[Example].materialise
      "has an apply method" in forAll { v: String =>
        Example(v) should be(AnyString(v))
      }
      "has an unapply method" in forAll { v: String =>
        (Example(v) match { case Example(w) => w }) should be(v)
      }
      "has a parse method" in forAll { v: String =>
        Example.parse(v) should be(Right(AnyString(v)))
      }
      "has a unsafeParse method" in forAll { v: String =>
        Example.unsafeParse(v) should be(AnyString(v))
      }
    }
    "CharacterClass" - {
      import Spec._
      type Example = CharacterClass['a' - 'z']
      val Example = RopeCompanion[Example]
      "has a from method" in forAll(Gen.alphaLowerChar) { c =>
        Example.from(c).right.get.value should be(c)
      }
      "has a unsafeFrom method" in forAll(Gen.alphaLowerChar) { c =>
        Example.unsafeFrom(c).value should be(c)
      }
      "has an unapply method" - {
        "which matches classes with the same spec" in forAll(Gen.alphaLowerChar) { c =>
          (Example.unsafeFrom(c) match {
            case Example(w) => w
          }) should be(c)
        }
        "which cannot match classes with a different spec" in forAll(Gen.alphaLowerChar) { c =>
          val value = CharacterClass.unsafeFrom['A' - 'z'](c)
          """value match { case Example(w) => w }""" shouldNot typeCheck
        }
      }
      "has a parse method" in forAll(Gen.alphaLowerChar) { c =>
        Example.parse(c.toString).right.get.value should be(c)
      }
      "has a unsafeParse method" in forAll(Gen.alphaLowerChar) { c =>
        Example.unsafeParse(c.toString).value should be(c)
      }
    }
    "Concat" - {
      import Spec._
      val genValues = Gen.zip(
        Gen.alphaLowerChar.map(CharacterClass.from['a' - 'z'](_).right.get),
        arbitrary[String].map(AnyString.apply)
      )
      "With two sections" - {
        type Example = Concat[CharacterClass['a' - 'z'], AnyString]
        val Example = RopeCompanion[Example].materialise
        "has a from method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example(prefix, suffix) should be(Concat(prefix, suffix))
        }
        "has a parse method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example.parse(s"${prefix.write}${suffix.write}") should be(Right(Concat(prefix, suffix)))
        }
        "has a unsafeParse method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example.unsafeParse(s"${prefix.write}${suffix.write}") should be(Concat(prefix, suffix))
        }
      }
      "With many sections" - {
        type Example = Concat[CharacterClass['a' - 'z'],
                              Concat[CharacterClass['a' - 'z'], Concat[CharacterClass['a' - 'z'], AnyString]]]
        val Example = RopeCompanion[Example].materialise
        "has a from method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example(prefix, prefix, prefix, suffix) should be(Concat(prefix, Concat(prefix, Concat(prefix, suffix))))
        }
        "has a parse method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example.parse(s"${prefix.write}${prefix.write}${prefix.write}${suffix.write}") should be(
              Right(Concat(prefix, Concat(prefix, Concat(prefix, suffix)))))
        }
        "has a unsafeParse method" in forAll(genValues) {
          case (prefix, suffix) =>
            Example.unsafeParse(s"${prefix.write}${prefix.write}${prefix.write}${suffix.write}") should be(
              Concat(prefix, Concat(prefix, Concat(prefix, suffix))))
        }
      }
    }
    "Repeated" - {
      type Example = Repeated[1, 5, Literal['@']]
      val Example = RopeCompanion[Example]

      val values: Gen[List['@']] = Gen.choose(1, 5).flatMap(Gen.listOfN(_, '@'))

      def asRepeated(v: List['@']) = Repeated.unsafeFrom[1, 5, Literal['@']](v.map(Literal['@']))

      "has an from method taking a list" in forAll(values) { v: List['@'] =>
        Example.from(v.map(Literal['@'])) should be(Right(asRepeated(v)))
      }
      "has an unsafeFrom method taking a list" in forAll(values) { v: List['@'] =>
        Example.unsafeFrom(v.map(Literal['@'])) should be(asRepeated(v))
      }
      "has an from method taking varargs" in {
        Example.from(Literal['@']) should be(Right(asRepeated(List('@'))))
        Example.from(Literal['@'], Literal['@']) should be(Right(asRepeated(List('@', '@'))))
        Example.from(Literal['@'], Literal['@'], Literal['@']) should be(Right(asRepeated(List('@', '@', '@'))))
      }
      "has an unapply method" in forAll(values) { v: List['@'] =>
        (asRepeated(v) match { case Example(w) => w }) should be(v.map(Literal['@']))
      }
      "has a parse method" in forAll(values) { v: List['@'] =>
        Example.parse(v.mkString) should be(Right(asRepeated(v)))
      }
      "has a unsafeParse method" in forAll(values) { v: List['@'] =>
        Example.unsafeParse(v.mkString) should be(asRepeated(v))
      }
    }
    "ConvertedTo" - {
      type Example = Digit
      val Example = RopeCompanion[Example]

      "has a from method taking the target value" in forAll(Gen.choose(0, 9)) { v =>
        Example.from(v) should be(ConvertedTo.fromTarget[Digit#Source, Int](v))
      }
      "has an unsafeFrom method taking the target value" in forAll(Gen.choose(0, 9)) { v =>
        Example.unsafeFrom(v) should be(ConvertedTo.fromTarget[Digit#Source, Int](v).getOrElse(fail()))
      }

      "has a from method taking the source value" in forAll { s: Digit#Source =>
        Example.fromSource(s) should be(ConvertedTo.fromSource[Digit#Source, Int](s))
      }

      "has a parse method" in forAll(Gen.choose(0, 9)) { v =>
        Example.parse(v.toString) should be(ConvertedTo.fromTarget[Digit#Source, Int](v))
      }
      "has a unsafeParse method" in forAll(Gen.choose(0, 9)) { v =>
        Example.unsafeParse(v.toString) should be(ConvertedTo.fromTarget[Digit#Source, Int](v).getOrElse(fail()))
      }
      "has a unapply method" in forAll(Gen.choose(0, 9)) { v =>
        (Example.unsafeFrom(v) match { case Example(t) => t }) should be(v)
      }
    }
    "Or" - {
      type Example = Literal['a'] Or Literal['b']
      val Example = RopeCompanion[Example]

      "has a from method taking an Either" in {
        Example.from(Left(Literal['a'])) should be(Or.First(Literal['a']))
        Example.from(Right(Literal['b'])) should be(Or.Second(Literal['b']))
      }

      "has a parse method" in forAll(Gen.oneOf(Literal['a'], Literal['b'])) { v =>
        Example.parse(v.value.toString) should be(Rope.parseTo[Example](v.value.toString))
      }
      "has a unsafeParse method" in forAll(Gen.oneOf(Literal['a'], Literal['b'])) { v =>
        Example.unsafeParse(v.value.toString) should be(Rope.parseTo[Example](v.value.toString).getOrElse(fail()))
      }
    }
  }
}
