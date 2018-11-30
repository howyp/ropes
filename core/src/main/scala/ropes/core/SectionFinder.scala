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

package ropes.core

trait SectionFinder[R <: Rope, SectionNumber <: Int with Singleton] {
  def apply(concat: R): Rope
}
object SectionFinder {
  implicit def section1[Section1 <: Section, Section2 <: Rope]: SectionFinder[Concat[Section1, Section2], 1] =
    concat => concat.prefix

  implicit def section2forSection[Section1 <: Section, Section2 <: Section]
    : SectionFinder[Concat[Section1, Section2], 2] =
    concat => concat.suffix

  implicit def section2forConcat[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 1]): SectionFinder[Concat[Prefix, Suffix], 2] =
    concat => nested(concat.suffix)

  implicit def section3[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 2]): SectionFinder[Concat[Prefix, Suffix], 3] =
    concat => nested(concat.suffix)

  implicit def section4[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 3]): SectionFinder[Concat[Prefix, Suffix], 4] =
    concat => nested(concat.suffix)
}
