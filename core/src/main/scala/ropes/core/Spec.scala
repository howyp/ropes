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

sealed trait Spec
object Spec {
  sealed trait Nestable                                                    extends Spec
  sealed trait *                                                           extends Spec
  sealed trait ==[V <: Char with Singleton]                                extends Nestable
  sealed trait ||[L <: Nestable, R <: Nestable]                            extends Nestable
  sealed trait &^[L <: Spec, R <: Nestable]                                extends Nestable
  sealed trait -[Start <: Char with Singleton, End <: Char with Singleton] extends Nestable
}
