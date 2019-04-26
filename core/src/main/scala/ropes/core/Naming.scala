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

sealed trait Naming
object Naming {
  sealed trait Unassigned                           extends Naming
  sealed trait Assigned[N <: String with Singleton] extends Naming
}

trait NameOps[R <: Rope] {
  //This is a safe implementation *only* because the name is a phantom type
  def assignName[N <: String with Singleton]: R Named N = this.asInstanceOf[R Named N]
  def unassignName: R                                   = this.asInstanceOf[R]
}
