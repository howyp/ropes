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

trait Parse[R <: Rope] {
  def parse(string: String): Parse.Result[R]
}
object Parse {
  def apply[R <: Rope](implicit parse: Parse[R]): Parse[R] = parse

  sealed trait Result[+R <: Rope] {
    def flatMap[S <: Rope](f: (R, String) => Result[S]): Result[S] = this match {
      case Result.Failure          => Result.Failure
      case Result.Complete(v)      => f(v, "")
      case Result.Incomplete(v, r) => f(v, r)
    }
  }
  object Result {
    case object Failure             extends Result[Nothing]
    sealed trait Success[R <: Rope] extends Result[R]

    object Success {
      def apply[R <: Rope](value: R, remaining: String): Success[R] = remaining match {
        case ""       => Complete(value)
        case nonEmpty => new Incomplete(value, nonEmpty) {}
      }
    }

    final case class Complete[R <: Rope](value: R)                                extends Success[R]
    sealed abstract case class Incomplete[R <: Rope](value: R, remaining: String) extends Success[R]
  }
}
