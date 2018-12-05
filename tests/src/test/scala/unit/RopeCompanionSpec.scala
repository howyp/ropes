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

package unit
import org.scalatest.{FreeSpec, Matchers}
import ropes.core._

class RopeCompanionSpec extends FreeSpec with Matchers {
  "The Rope companion object" - {
    "Can parse to a given rope" - {
      "Successfully if the parse is complete" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Success(AnyString(""), "")
        Rope.parseTo[AnyString]("") should be(Right(AnyString("")))
      }
      "Failing if the parse is incomplete" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Success(AnyString(""), "remaining")
        Rope.parseTo[AnyString]("") should be(Left(Rope.InvalidValue))
      }
      "Failing if the parse is a failure" in {
        implicit val exampleParse: Parse[AnyString] = _ => Parse.Result.Failure
        Rope.parseTo[AnyString]("") should be(Left(Rope.InvalidValue))
      }
    }
  }
}
