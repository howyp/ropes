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

package ropes.generic

import org.scalatest.{FreeSpec, Matchers}
import ropes.core.{Range, _}
import shapeless._

object R {
  type ExampleRope = Concat[Range['a', 'z'], Range['a', 'z']] //ConvertedTo ExampleCaseClass
}

import R._

trait ToHList[R <: Rope] {
  type Out
  def apply(r: R): Out
}
object ToHList {
  type Aux[R <: Rope, Out_0] = ToHList[R] { type Out = Out_0 }

  def instance[R <: Rope, Out_0](f: R => Out_0): ToHList.Aux[R, Out_0] = new ToHList[R] {
    type Out = Out_0
    def apply(r: R): Out_0 = f(r)
  }

  //TODO this will not work for nested concats
  implicit def toHlistForConcatPrefix[
      Prefix <: Rope,
      Suffix <: Rope
  ]: ToHList.Aux[Concat[Prefix, Suffix], Prefix :: Suffix :: HNil] =
    ToHList.instance(r => r.prefix :: r.suffix :: HNil)
}
trait FromHList[Repr] {
  type Out <: Rope
  def apply(r: Repr): Out
}
object FromHList {
  type Aux[R, Out_0 <: Rope] = FromHList[R] { type Out = Out_0 }

  def instance[R, Out_0 <: Rope](f: R => Out_0): FromHList.Aux[R, Out_0] = new FromHList[R] {
    type Out = Out_0
    def apply(r: R): Out_0 = f(r)
  }

  //TODO this will not work for nested concats
  implicit def fromHlistForConcatPrefix[
      Prefix <: Rope,
      Suffix <: Rope
  ]: FromHList.Aux[Prefix :: Suffix :: HNil, Concat[Prefix, Suffix]] =
    FromHList.instance {
      case head :: tail :: HNil => Concat(head, tail)
    }
}

case class ExampleCaseClass(first: Range['a', 'z'], second: Range['a', 'z'])
trait X {

  implicit def convertGeneric[Repr <: HList](
      implicit gen: Generic.Aux[ExampleCaseClass, Repr],
      toHList: ToHList.Aux[ExampleRope, Repr],
      fromHList: FromHList.Aux[Repr, ExampleRope]
  ): Conversion[Concat[Range['a', 'z'], Range['a', 'z']], ExampleCaseClass] =
    Conversion.instance[Concat[Range['a', 'z'], Range['a', 'z']], ExampleCaseClass](
      forwards = r => gen.from(toHList(r)),
      backwards = t => Right(fromHList(gen.to(t)))
    )
}
class GenericSpec extends FreeSpec with Matchers with X {

  "A rope can be converted to a case class with matching names" in {
    val r = Rope.parseTo[ExampleRope]("ab").right.get
    ConvertedTo.fromSource[ExampleRope, ExampleCaseClass](r).value should be(
      ExampleCaseClass(Range.unsafeFrom('a'), Range.unsafeFrom('b')))
  }
}
