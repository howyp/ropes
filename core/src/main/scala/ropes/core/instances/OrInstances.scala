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
import ropes.core.RopeCompanion.Build
import ropes.core.{Or, Parse, Parsing, Rope, Write}

trait OrInstances {
  implicit def parseOr[First <: Rope, Second <: Rope](implicit parseFirst: Parse[First],
                                                      parseSecond: Parse[Second]): Parse[First Or Second] = { str =>
    parseFirst.parse(str).map(Or.First.apply) match {
      case Parse.Result.Failure => parseSecond.parse(str).map(Or.Second.apply)
      case success              => success
    }
  }

  implicit def writeOr[First <: Rope, Second <: Rope](implicit writeFirst: Write[First],
                                                      writeSecond: Write[Second]): Write[First Or Second] = {
    case Or.First(v)  => writeFirst.write(v)
    case Or.Second(v) => writeSecond.write(v)
  }

  class OrCompanion[First <: Rope, Second <: Rope](protected val parseInstance: Parse[Or[First, Second]])
      extends Parsing[Or[First, Second]] {
    def from(e: Either[First, Second]): Or[First, Second] = Or.from(e)
  }

  implicit def writeBuild[First <: Rope, Second <: Rope](
      implicit parse: Parse[Or[First, Second]]): Build.Aux[Or[First, Second], OrCompanion[First, Second]] =
    Build.instance(new OrCompanion[First, Second](parse))
}
