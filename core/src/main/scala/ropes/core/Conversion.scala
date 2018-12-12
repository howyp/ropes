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

trait Conversion[Source <: Rope, Target] {
  def forwards(source: Source): Target
  def backwards(target: Target): Either[Conversion.Failed, Source]
}
object Conversion {
  final type Failed = Failed.type
  final case object Failed

  def instance[Source <: Rope, Target](
      forwards: Source => Target,
      backwards: Target => Either[Conversion.Failed, Source]): Conversion[Source, Target] = {
    val f = forwards
    val b = backwards
    new Conversion[Source, Target] {
      def forwards(source: Source)  = f(source)
      def backwards(target: Target) = b(target)
    }
  }
}
