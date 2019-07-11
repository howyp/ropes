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

import ropes.core.Rope.InvalidValue

trait Parsing[R <: Rope] {
  def parse(s: String)(implicit parse: Parse[R]): Either[InvalidValue.type, R] = Rope.parseTo[R](s)(parse)
  def unsafeParse(s: String)(implicit parse: Parse[R]): R = {
    Rope.parseTo[R](s)(parse).getOrElse(throw new IllegalArgumentException(s"'$s' is invalid"))
  }
}
object RopeCompanion {
  trait Build[R <: Rope] {
    type Companion
    def companion: Companion
  }
  object Build {
    type Aux[R <: Rope, _Companion] = Build[R] { type Companion = _Companion }

    def instance[R <: Rope, _Companion](c: _Companion) = new Build[R] {
      type Companion = _Companion
      def companion: _Companion = c
    }
  }

  def apply[R <: Rope](implicit build: RopeCompanion.Build[R]): build.Companion = build.companion
}
