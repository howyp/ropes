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

package ropes.instances

import ropes.{Parse, Range, Rope, Write}

private[ropes] trait RangeInstances {
  implicit def rangeParse[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Parse[Range[Start, End]] = { str =>
    str.headOption
      .toRight(left = Rope.InvalidValue)
      .flatMap(Range.from[Start, End](_))
      .map(Parse.Result.Success(_, str.tail))
      .getOrElse(Parse.Result.Failure)
  }

  implicit def rangeWrite[Start <: Char with Singleton, End <: Char with Singleton]: Write[Range[Start, End]] =
    _.value.toString
}
