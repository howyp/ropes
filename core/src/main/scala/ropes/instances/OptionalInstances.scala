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

import ropes.{Optional, Parse, Rope, Write}

private[ropes] trait OptionalInstances {
  implicit def optionalParse[R <: Rope](implicit parse: Parse[R]): Parse[Optional[R]] = { original =>
    parse.parse(original) match {
      case Parse.Result.Failure     => Parse.Result.Success(Optional(None), original)
      case Parse.Result.Complete(r) => Parse.Result.Complete(Optional(Some(r)))
    }
  }

  implicit def optionalWrite[R <: Rope](implicit write: Write[R]): Write[Optional[R]] =
    _.value.map(write.write).getOrElse("")
}
