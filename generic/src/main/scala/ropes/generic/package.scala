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

package ropes

import ropes.core.{Conversion, Rope}
import shapeless.{Generic, HList}

package object generic {

  implicit def convertGeneric[R <: Rope, Out, Repr <: HList](
      implicit gen: Generic.Aux[Out, Repr],
      toHList: ToGeneric.Aux[R, Repr],
      fromHList: FromGeneric.Aux[Repr, R]
  ): Conversion[R, Out] =
    Conversion.instance[R, Out](
      forwards = r => gen.from(toHList(r)),
      backwards = t => Right(fromHList(gen.to(t)))
    )
}
