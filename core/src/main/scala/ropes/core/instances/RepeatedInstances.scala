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

import scala.annotation.tailrec

trait RepeatedInstances {
  implicit def repeatedParse[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope](
      implicit
      min: ValueOf[MinReps],
      max: ValueOf[MaxReps],
      r: Parse[R]): Parse[Repeated[MinReps, MaxReps, R]] = { originalString =>
    @tailrec
    def repeatedParseUntilFailureOrMax(str: String, listSoFar: List[R]): List[R] = {
      r.parse(str) match {
        case Parse.Result.Failure                  => listSoFar
        case Parse.Result.Complete(r)              => listSoFar :+ r
        case Parse.Result.Incomplete(r, remaining) => repeatedParseUntilFailureOrMax(remaining, listSoFar :+ r)
      }
    }
    Repeated.from[MinReps, MaxReps, R](repeatedParseUntilFailureOrMax(originalString, List.empty)) match {
      case Left(_)       => Parse.Result.Failure
      case Right(values) => Parse.Result.Success(values, "")
    }
  }

  implicit def repeatedWrite[MinReps <: Int with Singleton, MaxReps <: Int with Singleton, R <: Rope](
      implicit w: Write[R]): Write[Repeated[MinReps, MaxReps, R]] = _.values.map(_.write).mkString("")
}
