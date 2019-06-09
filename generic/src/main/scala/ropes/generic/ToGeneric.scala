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

trait ToGeneric[R <: Rope] {
  type Out
  def apply(r: R): Out
}

trait LowPriorityToGeneric {
  implicit def toGenericForNonNestedConcat[
      Prefix <: Rope,
      Suffix <: Rope
  ]: ToGeneric.Aux[Concat[Prefix, Suffix], Prefix :: Suffix :: HNil] =
    ToGeneric.instance(r => r.prefix :: r.suffix :: HNil)
}

object ToGeneric extends LowPriorityToGeneric {
  type Aux[R <: Rope, Out_0] = ToGeneric[R] { type Out = Out_0 }

  def instance[R <: Rope, Out_0](f: R => Out_0): ToGeneric.Aux[R, Out_0] = new ToGeneric[R] {
    type Out = Out_0
    def apply(r: R): Out_0 = f(r)
  }

  implicit def toGenericForNestedConcat[
      Prefix <: Rope,
      Suffix1 <: Rope,
      Suffix2 <: Rope,
      Nested <: HList
  ](implicit suffixToGeneric: ToGeneric.Aux[Concat[Suffix1, Suffix2], Nested])
    : ToGeneric.Aux[Concat[Prefix, Concat[Suffix1, Suffix2]], Prefix :: Nested] =
    ToGeneric.instance(r => r.prefix :: suffixToGeneric(r.suffix))
}
