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

import ropes.core._

private[ropes] trait OptionalInstances {
  implicit def optionConversionFromRepeated[R <: Rope] = Conversion.instance[Repeated[0, 1, R], Option[R]](
    forwards = _.values.headOption,
    backwards = opt => Repeated.from[0, 1, R](opt.toList).swap.map(_ => Conversion.Failed).swap
  )
}
