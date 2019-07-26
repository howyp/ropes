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
import ropes.core._

private[ropes] trait ConvertedToInstances {
  implicit def convertedToParse[Source <: Rope, Target, N <: Naming](
      implicit sourceParse: Parse[Source],
      conversion: Conversion[Source, Target]): Parse[ConvertedTo[Source, Target] { type Name = N }] =
    sourceParse.parse(_).flatMap { (source, remainder) =>
      Parse.Result.Success(
        ConvertedTo.fromSource[Source, Target](source).asInstanceOf[ConvertedTo[Source, Target] { type Name = N }],
        remainder)
    }

  implicit def convertedToWrite[Source <: Rope, Target, N <: Naming](
      implicit sourceWrite: Write[Source],
      conversion: Conversion[Source, Target]): Write[ConvertedTo[Source, Target] { type Name = N }] =
    target => sourceWrite.write(conversion.backwards(target.value).getOrElse(throw new IllegalStateException))

  class ConvertedToCompanion[S <: Rope, T](protected val parseInstance: Parse[ConvertedTo[S, T]],
                                           conversion: Conversion[S, T])
      extends Parsing[ConvertedTo[S, T]] {
    def from(target: T): Either[Rope.InvalidValue.type, ConvertedTo[S, T]] =
      ConvertedTo.fromTarget(target)(conversion)

    def fromSource(target: S): ConvertedTo[S, T] =
      ConvertedTo.fromSource(target)(conversion)

    def unsafeFrom(target: T): ConvertedTo[S, T] =
      from(target).getOrElse(throw new IllegalArgumentException(target.toString))

    def unapply(arg: ConvertedTo[S, T]): Option[T] = Some(arg.value)
  }

  implicit def convertedToBuild[S <: Rope, T](
      implicit parse: Parse[ConvertedTo[S, T]],
      conversion: Conversion[S, T]): Build.Aux[ConvertedTo[S, T], ConvertedToCompanion[S, T]] =
    Build.instance(new ConvertedToCompanion[S, T](parse, conversion))
}
