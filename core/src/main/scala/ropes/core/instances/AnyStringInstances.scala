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

import ropes.core.RopeCompanion.Build
import ropes.core._

private[ropes] trait AnyStringInstances {
  implicit def anyStringParse[A <: AnyString]: Parse[A] = str => Parse.Result.Complete(AnyString(str).asInstanceOf[A])

  implicit val anyStringWrite: Write[AnyString] = _.value

  class AnyStringCompanion[A <: AnyString](implicit ev: A =:= AnyString) extends Parsing[A] { self =>
    protected val parseInstance: Parse[A] = Parse[A]

    def apply(value: String): A         = AnyString(value).asInstanceOf[A]
    def unapply(arg: A): Option[String] = Some(arg.value)
  }
  implicit def anyStringBuild[A <: AnyString](implicit ev: A =:= AnyString): Build.Aux[A, AnyStringCompanion[A]] =
    Build.instance(new AnyStringCompanion[A])
}
