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

import ropes.core._

package object dsl {
  //TODO Perhaps this should be specified next to the types itself? I guess this comes down to whether we want a
  //separation between the main types and a DSL, or the main types and the _ops_
  type +:[Prefix <: Rope, Suffix <: Rope] = Concat[Prefix, Suffix]
  val +: = Concat

  implicit class RopeOps[Suffix <: Rope](suffix: Suffix) {
    def +:[Prefix <: Rope](prefix: Prefix): +:[Prefix, Suffix] = Concat(prefix, suffix)
  }

  implicit class LiteralOps[Suffix <: Rope](suffix: Suffix) {
    def +:[Literal <: Char with Singleton](prefix: Literal): Concat[Exactly[Literal], Suffix] =
      Concat(Exactly[Literal](prefix), suffix)
  }
}
