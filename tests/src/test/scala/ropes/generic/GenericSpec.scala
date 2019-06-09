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

class GenericSpec extends FreeSpec with Matchers {
  case class ExampleCaseClass(first: Range['a', 'z'], second: Range['a', 'z'])
  type ExampleRope = Concat[Range['a', 'z'], Range['a', 'z']] //ConvertedTo ExampleCaseClass

  "A rope can be converted to a case class" in {
    val r = Rope.parseTo[ExampleRope]("ab").right.get
    ConvertedTo.fromSource[ExampleRope, ExampleCaseClass](r).value should be(
      ExampleCaseClass(Range.unsafeFrom('a'), Range.unsafeFrom('b')))
  }
}
