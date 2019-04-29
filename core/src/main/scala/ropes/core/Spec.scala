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

package ropes.core

sealed trait Spec

object Spec {
  sealed trait Nestable extends Spec
  type * = *.type
  case object *                                                                                  extends Spec
  case class ==[V <: Char with Singleton](value: V)                                              extends Nestable
  case class ||[L <: Nestable, R <: Nestable](left: L, right: R)                                 extends Nestable
  case class &^[L <: Spec, R <: Nestable](left: L, right: R)                                     extends Nestable
  case class -[Start <: Char with Singleton, End <: Char with Singleton](start: Start, end: End) extends Nestable

//  implicit def `validate-`[Start <: Char with Singleton, End <: Char with Singleton](
//      implicit start: ValueOf[Start],
//      end: ValueOf[End]
//  ): Reduce[Start - End] = ???
}
