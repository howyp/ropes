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
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ropes.core._

class RopeCompanionSpec extends FreeSpec with Matchers with ScalaCheckDrivenPropertyChecks {
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
        val A: A = RopeCompanion[A]
        A.value should be('a')
      }
    }
    "AnyString" - {
      type Example = AnyString
      val Example = RopeCompanion[AnyString].materialise
      "has an apply method" in forAll { v: String =>
        Example(v) should be(AnyString(v))
      }
      "has an unapply method" in forAll { v: String =>
        (Example(v) match {
          case Example(w) => w
        }) should be(v)
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
    }
    "Concat" - {
      import Spec._
      "With two sections" - {
        type Example = Concat[CharacterClass['a' - 'z'], AnyString]
        val Example = RopeCompanion[Example].materialise
        "has a from method" in forAll(Gen.alphaLowerChar, arbitrary[String]) { (c, s) =>
          val prefix = CharacterClass.from['a' - 'z'](c).right.get
          val suffix = AnyString(s)
          Example(prefix, suffix) should be(Concat(prefix, suffix))
        }
      }
      "With many sections" - {
        type Example = Concat[CharacterClass['a' - 'z'],
                              Concat[CharacterClass['a' - 'z'], Concat[CharacterClass['a' - 'z'], AnyString]]]
        val Example = RopeCompanion[Example].materialise
        "has a from method" in forAll(Gen.alphaLowerChar, arbitrary[String]) { (c, s) =>
          val prefix = CharacterClass.from['a' - 'z'](c).right.get
          val suffix = AnyString(s)
          Example(prefix, prefix, prefix, suffix) should be(Concat(prefix, Concat(prefix, Concat(prefix, suffix))))
        }
      }
    }
  }
}
