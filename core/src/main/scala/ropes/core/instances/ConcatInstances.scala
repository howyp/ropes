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

import ropes.core.{Concat, Parse, Rope, SectionFinder, Write}

private[ropes] trait ConcatExplicitSectionFinderInstances {
  implicit def section1[Section1 <: Rope, Section2 <: Rope]
    : SectionFinder.Aux[Concat[Section1, Section2], 1, Section1] = SectionFinder.instance(_.prefix)

  implicit def section2forSection[Section1 <: Rope, Section2 <: Rope]
    : SectionFinder.Aux[Concat[Section1, Section2], 2, Section2] = SectionFinder.instance(_.suffix)
}
private[ropes] trait ConcatGeneratedSectionFinderInstances extends ConcatExplicitSectionFinderInstances {
  implicit def section2forConcat[Prefix <: Rope, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 1]): SectionFinder.Aux[Concat[Prefix, Suffix], 2, nested.Out] =
    SectionFinder.instance(concat => nested(concat.suffix))

  implicit def section3[Prefix <: Rope, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 2]): SectionFinder.Aux[Concat[Prefix, Suffix], 3, nested.Out] =
    SectionFinder.instance(concat => nested(concat.suffix))

  implicit def section4[Prefix <: Rope, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 3]): SectionFinder.Aux[Concat[Prefix, Suffix], 4, nested.Out] =
    SectionFinder.instance(concat => nested(concat.suffix))

  implicit def section5[Prefix <: Rope, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 4]): SectionFinder.Aux[Concat[Prefix, Suffix], 5, nested.Out] =
    SectionFinder.instance(concat => nested(concat.suffix))
}
private[ropes] trait ConcatInstances extends ConcatGeneratedSectionFinderInstances {
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
