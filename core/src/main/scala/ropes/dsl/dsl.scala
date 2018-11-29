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

package object dsl {
  //TODO Perhaps this should be specified next to the types itself? I guess this comes down to whether we want a
  //separation between the main types and a DSL, or the main types and the _ops_
  type :+[Prefix <: Rope, Suffix <: Rope] = Concat[Prefix, Suffix]
  val :+ = Concat

  type -->[Start <: Char with Singleton, End <: Char with Singleton] = Range[Start, End]

  implicit class RopeOps[Prefix <: Rope](prefix: Prefix) {
    def :+[Suffix <: Rope](suffix: Suffix): Concat[Prefix, Suffix]                   = Concat(prefix, suffix)
    def :+[Suffix <: Rope](suffix: Option[Suffix]): Concat[Prefix, Optional[Suffix]] = Concat(prefix, Optional(suffix))
  }

  implicit class LiteralOps[Literal <: Char with Singleton](literal: Literal)(implicit Literal: ValueOf[Literal])
      extends RopeOps(prefix = Exactly[Literal](literal)) {
    def -->[OtherLiteral <: Char with Singleton](otherLiteral: OtherLiteral)(value: Char)(
        implicit OtherLiteral: ValueOf[OtherLiteral]): Either[Rope.InvalidValue.type, Literal --> OtherLiteral] =
      Range.from[Literal, OtherLiteral](value)
  }
}
