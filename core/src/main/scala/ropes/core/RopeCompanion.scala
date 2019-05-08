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

object RopeCompanion {
  trait Build[R <: Rope] {
    type Companion
    def companion: Companion
  }
  object Build {
    type Aux[R <: Rope, _Companion] = Build[R] { type Companion = _Companion }
  }

  //TODO move these into their specific instances traits
  implicit def literalBuild[V <: Char with Singleton](implicit v: ValueOf[V]): Build.Aux[Literal[V], Literal[V]] =
    new Build[Literal[V]] {
      type Companion = Literal[V]
      def companion: Literal[V] = Literal(v.value)
    }

  object AnyStringCompanion { self =>
    final val materialise    = self
    def apply(value: String) = AnyString.apply(value)
  }
  implicit val anyStringBuild: Build.Aux[AnyString, AnyStringCompanion.type] =
    new Build[AnyString] {
      type Companion = AnyStringCompanion.type
      def companion: AnyStringCompanion.type = AnyStringCompanion
    }

  def apply[R <: Rope](implicit build: RopeCompanion.Build[R]): build.Companion = build.companion
  def build[R <: Rope](implicit build: RopeCompanion.Build[R]): build.Companion = build.companion
}
