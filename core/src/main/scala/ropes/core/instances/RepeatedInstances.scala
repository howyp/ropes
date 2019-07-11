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

import scala.annotation.tailrec

trait RepeatedInstances {
  implicit def repeatedParse[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope, N <: Naming](
      implicit
      min: ValueOf[MinReps],
      max: ValueOf[MaxReps],
      parseR: Parse[R]): Parse[Repeated[MinReps, MaxReps, R] { type Name = N }] = { originalString =>
    @tailrec
    def repeatedParseUntilFailureOrMax(listSoFar: List[R], str: String): (List[R], String) =
      if (listSoFar.size == max.value) listSoFar -> str
      else
        parseR.parse(str) match {
          case Parse.Result.Failure                  => listSoFar -> str
          case Parse.Result.Complete(v)              => (listSoFar :+ v) -> ""
          case Parse.Result.Incomplete(v, remaining) => repeatedParseUntilFailureOrMax(listSoFar :+ v, remaining)
        }

    val (list, remaining) = repeatedParseUntilFailureOrMax(List.empty, originalString)
    Repeated.from[MinReps, MaxReps, R](list) match {
      case Left(_) => Parse.Result.Failure
      case Right(values) =>
        Parse.Result.Success(values.asInstanceOf[Repeated[MinReps, MaxReps, R] { type Name = N }], remaining)
    }
  }

  implicit def repeatedWrite[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope, N <: Naming](
      implicit w: Write[R]): Write[Repeated[MinReps, MaxReps, R] { type Name = N }] = _.values.map(_.write).mkString("")

  class RepeatedCompanion[MinReps <: Int with Singleton: ValueOf, MaxReps <: Int with Singleton: ValueOf, R <: Rope]
      extends Parsing[Repeated[MinReps, MaxReps, R]] { self =>

    def from(values: List[R]): Either[Rope.InvalidValue.type, Repeated[MinReps, MaxReps, R]] =
      Repeated.from[MinReps, MaxReps, R](values)

    def from(values: R*): Either[Rope.InvalidValue.type, Repeated[MinReps, MaxReps, R]] =
      Repeated.from[MinReps, MaxReps, R](values.toList)

    def unsafeFrom(values: List[R]): Repeated[MinReps, MaxReps, R] =
      Repeated.unsafeFrom[MinReps, MaxReps, R](values)

    def unsafeFrom(values: R*): Repeated[MinReps, MaxReps, R] =
      Repeated.unsafeFrom[MinReps, MaxReps, R](values.toList)

    def unapply(repeated: Repeated[MinReps, MaxReps, R]): Option[List[R]] = Some(repeated.values)
  }
  implicit def anyStringBuild[MinReps <: Int with Singleton: ValueOf, MaxReps <: Int with Singleton: ValueOf, R <: Rope]
    : Build.Aux[Repeated[MinReps, MaxReps, R], RepeatedCompanion[MinReps, MaxReps, R]] =
    Build.instance(new RepeatedCompanion[MinReps, MaxReps, R])
}
