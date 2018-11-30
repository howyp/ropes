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

trait SectionFinder[In <: Rope, SectionNumber <: Int with Singleton] {
  type Out <: Rope
  def apply(in: In): Out
}
object SectionFinder {
  type Aux[In <: Rope, SectionNumber <: Int with Singleton, _Out <: Rope] = SectionFinder[In, SectionNumber] {
    type Out = _Out
  }

  private def instance[In <: Rope, SectionNumber <: Int with Singleton, _Out <: Rope](
      f: In => _Out): SectionFinder.Aux[In, SectionNumber, _Out] = new SectionFinder[In, SectionNumber] {
    type Out = _Out
    def apply(in: In) = f(in)
  }

  implicit def section1[Section1 <: Section, Section2 <: Rope]
    : SectionFinder.Aux[Concat[Section1, Section2], 1, Section1] = instance { _.prefix }

  implicit def section2forSection[Section1 <: Section, Section2 <: Section]
    : SectionFinder.Aux[Concat[Section1, Section2], 2, Section2] = instance(_.suffix)

  implicit def section2forConcat[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 1]): SectionFinder.Aux[Concat[Prefix, Suffix], 2, nested.Out] =
    instance(concat => nested(concat.suffix))

  implicit def section3[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 2]): SectionFinder.Aux[Concat[Prefix, Suffix], 3, nested.Out] =
    instance(concat => nested(concat.suffix))

  implicit def section4[Prefix <: Section, Suffix <: Rope](
      implicit nested: SectionFinder[Suffix, 3]): SectionFinder.Aux[Concat[Prefix, Suffix], 4, nested.Out] =
    instance(concat => nested(concat.suffix))
}
