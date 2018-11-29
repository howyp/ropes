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

import ropes.core.{Concat, Parse, Rope, Write}

private[ropes] trait ConcatInstances {
  implicit def concatParse[Prefix <: Rope: Parse, Suffix <: Rope: Parse]: Parse[Concat[Prefix, Suffix]] = { str =>
    Parse[Prefix].parse(str).flatMap {
      case (prefix, afterSuffix) =>
        Parse[Suffix].parse(afterSuffix).flatMap {
          case (suffix, remaining) => Parse.Result.Success(Concat(prefix, suffix), remaining)
        }
    }
  }

  implicit def concatWrite[P <: Rope: Write, S <: Rope: Write]: Write[Concat[P, S]] =
    concat => concat.prefix.write + concat.suffix.write
}
