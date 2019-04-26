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

import ropes.core.{Concat, Naming, Parse, Rope, SectionFinder, Named, Write}

private[ropes] trait ConcatExplicitSectionFinderInstances {
  implicit def sectionByNumber1[Section1 <: Rope, Section2 <: Rope]
    : SectionFinder.Aux[Concat[Section1, Section2], 1, Section1] = SectionFinder.instance(_.prefix)

  implicit def sectionByNumber2forSection[Section1 <: Rope, Section2 <: Rope]
    : SectionFinder.Aux[Concat[Section1, Section2], 2, Section2] = SectionFinder.instance(_.suffix)

  implicit def sectionBySubNameSuffix[Prefix <: Rope, Suffix <: Rope, SectionName <: String with Singleton]
    : SectionFinder.Aux[Concat[Prefix, Suffix Named SectionName], SectionName, Suffix Named SectionName] =
    SectionFinder.instance(_.suffix)

  implicit def sectionBySubNamePrefix[Prefix <: Rope, Suffix <: Rope, SectionName <: String with Singleton]
    : SectionFinder.Aux[Concat[Prefix Named SectionName, Suffix], SectionName, Prefix Named SectionName] =
    SectionFinder.instance(_.prefix)

  implicit def sectionByName2ForConcat[Prefix <: Rope, Suffix <: Rope, SectionName <: String with Singleton](
      implicit nested: SectionFinder[Suffix, SectionName])
    : SectionFinder.Aux[Concat[Prefix, Suffix], SectionName, nested.Out] =
    SectionFinder.instance(concat => nested(concat.suffix))
}
private[ropes] trait ConcatInstances extends ConcatGeneratedSectionFinderInstances {
  implicit def concatParse[Prefix <: Rope: Parse, Suffix <: Rope: Parse, N <: Naming]
    : Parse[Concat[Prefix, Suffix] { type Name = N }] = { str =>
    Parse[Prefix].parse(str).flatMap {
      case (prefix, afterSuffix) =>
        Parse[Suffix].parse(afterSuffix).flatMap {
          case (suffix, remaining) =>
            Parse.Result.Success(Concat(prefix, suffix).asInstanceOf[Concat[Prefix, Suffix] { type Name = N }],
                                 remaining)
        }
    }
  }

  implicit def concatWrite[P <: Rope: Write, S <: Rope: Write, N <: Naming]: Write[Concat[P, S] { type Name = N }] =
    concat => concat.prefix.write + concat.suffix.write
}
