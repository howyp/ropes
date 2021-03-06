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

package ropes.core.instances

import ropes.core._

private[ropes] trait LiteralInstances {
  implicit def literalParseChar[C <: Char with Singleton, N <: Naming](
      implicit c: ValueOf[C]): Parse[Literal[C] { type Name = N }] = { str =>
    if (str.length > 0 && str.charAt(0) == c.value)
      Parse.Result.Success(Literal[C](c.value).asInstanceOf[Literal[C] { type Name = N }], str.substring(1))
    else Parse.Result.Failure
  }
  implicit def literalWriteChar[C <: Char with Singleton, N <: Naming]: Write[Literal[C] { type Name = N }] =
    _.value.toString
}
