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

object FromGeneric {
  type Aux[R, Out_0 <: Rope] = FromGeneric[R] { type Out = Out_0 }

  def instance[R, Out_0 <: Rope](f: R => Out_0): FromGeneric.Aux[R, Out_0] = new FromGeneric[R] {
    type Out = Out_0
    def apply(r: R): Out_0 = f(r)
  }

  //TODO this will not work for nested concats
  implicit def fromHlistForConcatPrefix[
      Prefix <: Rope,
      Suffix <: Rope
  ]: FromGeneric.Aux[Prefix :: Suffix :: HNil, Concat[Prefix, Suffix]] =
    FromGeneric.instance { case head :: tail :: HNil => Concat(head, tail) }

  implicit def any[In, Out <: Rope]: FromGeneric.Aux[In, Out] = FromGeneric.instance(_ => ???)
}
