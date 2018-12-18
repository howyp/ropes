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

private[ropes] trait NamedInstances {
  implicit def namedParse[Name <: String with Singleton, R <: Rope](implicit name: ValueOf[Name],
                                                                    rParse: Parse[R]): Parse[Named[R, Name]] =
    rParse.parse(_).map(Named(_, name.value))

  implicit def namedWrite[Name <: String with Singleton, R <: Rope](
      implicit
      rWrite: Write[R]): Write[Named[R, Name]] = n => rWrite.write(n.value)
}
