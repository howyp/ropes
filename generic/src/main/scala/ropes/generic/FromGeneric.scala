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

import ropes.core.{Concat, Rope}
import shapeless._

trait FromGeneric[Repr] {
  type Out <: Rope
  def apply(r: Repr): Out
}

trait LowPriorityFromGeneric {
  implicit def fromGenericForNonNestedConcat[
      Prefix <: Rope,
      Suffix <: Rope
  ]: FromGeneric.Aux[Prefix :: Suffix :: HNil, Concat[Prefix, Suffix]] =
    FromGeneric.instance { case head :: tail :: HNil => Concat(head, tail) }
}

object FromGeneric extends LowPriorityFromGeneric {
  type Aux[R, Out_0 <: Rope] = FromGeneric[R] { type Out = Out_0 }

  def instance[R, Out_0 <: Rope](f: R => Out_0): FromGeneric.Aux[R, Out_0] = new FromGeneric[R] {
    type Out = Out_0
    def apply(r: R): Out_0 = f(r)
  }

  implicit def fromGenericForNestedConcat[
      Prefix <: Rope,
      Suffix1 <: Rope,
      Suffix2 <: Rope,
      Nested <: HList
  ](implicit suffixToGeneric: FromGeneric.Aux[Nested, Concat[Suffix1, Suffix2]])
    : FromGeneric.Aux[Prefix :: Nested, Concat[Prefix, Concat[Suffix1, Suffix2]]] =
    FromGeneric.instance {
      case prefix :: nested => Concat(prefix, suffixToGeneric(nested))
    }
}
