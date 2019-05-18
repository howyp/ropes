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

case class ExampleCaseClass(first: Range['a', 'z'], second: Range['a', 'z'])
trait X {
  implicit def convertGeneric //(implicit generic: Generic[ExampleCaseClass])
    : Conversion[Concat[Range['a', 'z'], Range['a', 'z']], ExampleCaseClass] = {
    val gen = Generic[ExampleCaseClass]
    println(Typeable[gen.Repr].describe)
    Conversion.instance[Concat[Range['a', 'z'], Range['a', 'z']], ExampleCaseClass](
      forwards = r => gen.from(r.section[1] :: r.section[2] :: HNil),
      backwards = t => Right(Concat(t.first, t.second))
    )
  }
}
class GenericSpec extends FreeSpec with Matchers with X {

  type ExampleRope = Concat[Range['a', 'z'], Range['a', 'z']] //ConvertedTo ExampleCaseClass

  "A rope can be converted to a case class with matching names" in {

    val gen = Generic[ExampleCaseClass]
    println(Typeable[gen.type].describe)
    println(gen.to(ExampleCaseClass(Range.unsafeFrom['a', 'z']('a'), Range.unsafeFrom['a', 'z']('b'))))
    println(gen.from(Range.unsafeFrom['a', 'z']('a') :: Range.unsafeFrom['a', 'z']('b') :: HNil))

    val r = Rope.parseTo[ExampleRope]("ab").right.get
    println(gen.from(r.section[1] :: r.section[2] :: HNil))

    Conversion.instance[Concat[Range['a', 'z'], Range['a', 'z']], ExampleCaseClass](
      forwards = r => gen.from((r.section[1]: Range['a', 'z']) :: (r.section[2]: Range['a', 'z']) :: HNil),
      backwards = _ => ???
    )

    ConvertedTo.fromSource[ExampleRope, ExampleCaseClass](r).value should be(
      ExampleCaseClass(Range.unsafeFrom['a', 'z']('a'), Range.unsafeFrom['a', 'z']('b')))
  }
}
