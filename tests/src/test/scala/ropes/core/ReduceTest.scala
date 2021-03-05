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

import org.scalatest.{FreeSpec, Matchers}
import ropes.core.Spec._

class ReduceTest extends FreeSpec with Matchers {

  "Specs reduce down to ranges" - {
    "==['a']" in {
      Reduce[==['a']].reduce should contain only ('a' -> 'a')
    }
    "|| with" - {
      "two literals" - {
        "==['a'] || ==['b']" in (Reduce[==['a'] || ==['b']].reduce should contain only ('a'        -> 'b'))
        "==['b'] || ==['a']" in (Reduce[==['b'] || ==['a']].reduce should contain only ('a'        -> 'b'))
        "==['a'] || ==['c']" in (Reduce[==['a'] || ==['c']].reduce should contain inOrderOnly ('a' -> 'a', 'c' -> 'c'))
        "==['c'] || ==['a']" in (Reduce[==['c'] || ==['a']].reduce should contain inOrderOnly ('a' -> 'a', 'c' -> 'c'))
      }

      "three literals" - {
        "==['a'] || ==['b'] || ==['c']" in (Reduce[
          ==['a'] || ==['b'] || ==['c']
        ].reduce should contain only ('a'        -> 'c'))
        "==['a'] || ==['c'] || ==['b']" in (Reduce[
          ==['a'] || ==['c'] || ==['b']
        ].reduce should contain only ('a'        -> 'c'))
        "==['c'] || ==['b'] || ==['a']" in (Reduce[
          ==['c'] || ==['b'] || ==['a']
        ].reduce should contain only ('a'        -> 'c'))
        "==['c'] || ==['d'] || ==['a']" in (Reduce[
          ==['c'] || ==['d'] || ==['a']
        ].reduce should contain inOrderOnly ('a' -> 'a', 'c' -> 'd'))
      }
      "two -" - {
        "('a' - 'c') || ('f' -'z')" in (Reduce[
          ('a' - 'c') || ('f' - 'z')
        ].reduce should contain inOrderOnly ('a'                                                           -> 'c', 'f' -> 'z'))
        "('a' - 'c') || ('d' -'z')" in (Reduce[('a' - 'c') || ('d' - 'z')].reduce should contain only ('a' -> 'z'))
        "('a' - 'e') || ('b' -'z')" in (Reduce[('a' - 'c') || ('d' - 'z')].reduce should contain only ('a' -> 'z'))
      }
    }
    "- with" - {
      "two different characters in the correct order" - {
        "('a' - 'z')" in (Reduce[('a' - 'z')].reduce should contain only ('a' -> 'z'))
        "('a' - 'b')" in (Reduce[('a' - 'b')].reduce should contain only ('a' -> 'b'))
      }
      "two different characters in the wrong order" - {
        "('b' - 'a')" in (an[Exception] should be thrownBy Reduce[('b' - 'a')].reduce)
        "('9' - '1')" in (an[Exception] should be thrownBy Reduce[('9' - '1')].reduce)
      }
      "the same characters" - {
        "('a' - 'a')" in (Reduce[('a' - 'a')].reduce should contain only ('a' -> 'a'))
      }
    }
    "*" in (Reduce[*].reduce should contain only (Char.MinValue -> Char.MaxValue))
    "&^ with" - {
      "* and a literal char" - {
        "* &^ ==['c']" in (Reduce[
          * &^ ==['c']
        ].reduce should contain inOrderOnly (Char.MinValue -> 'b', 'd' -> Char.MaxValue))
      }
      "* and a '-'" - {
        "* &^ ('c' - 'e')" in (Reduce[
          * &^ ('c' - 'e')
        ].reduce should contain inOrderOnly (Char.MinValue -> 'b', 'f' -> Char.MaxValue))
      }
      "* and multiple literal chars" - {
        "* &^ (==['a'] || ==['g'])" in (Reduce[* &^ (==['a'] || ==['g'])].reduce should contain inOrderOnly (
          Char.MinValue -> '`',
          'b'           -> 'f',
          'h'           -> Char.MaxValue
        ))
      }
      "a literal char and a literal char" - {
        "==['c'] &^ ==['c']" in (Reduce[==['c'] &^ ==['c']].reduce should be(empty))
        "==['c'] &^ ==['d']" in (Reduce[==['c'] &^ ==['d']].reduce should contain only ('c' -> 'c'))
        "==['c'] &^ ==['a']" in (Reduce[==['c'] &^ ==['a']].reduce should contain only ('c' -> 'c'))
      }
      "a - and a literal" - {
        "'a' - 'z' &^ ==['c']" in (Reduce[
          'a' - 'z' &^ ==['c']
        ].reduce should contain inOrderOnly ('a' -> 'b', 'd' -> 'z'))

        "'a' - 'z' &^ ==['a']" in (Reduce['a' - 'z' &^ ==['a']].reduce should contain only ('b' -> 'z'))
        "'a' - 'z' &^ ==['z']" in (Reduce['a' - 'z' &^ ==['z']].reduce should contain only ('a' -> 'y'))
        "'a' - 'z' &^ ==['A']" in (Reduce['a' - 'z' &^ ==['A']].reduce should contain only ('a' -> 'z'))
      }
      "a - and a -" - {
        "'a' - 'z' &^ ('c' - 'e')" in (Reduce[
          'a' - 'z' &^ ('c' - 'e')
        ].reduce should contain inOrderOnly ('a'                                                        -> 'b', 'f' -> 'z'))
        "'a' - 'z' &^ ('a' - 'e')" in (Reduce['a' - 'z' &^ ('a' - 'e')].reduce should contain only ('f' -> 'z'))
        "'a' - 'z' &^ ('c' - 'z')" in (Reduce['a' - 'z' &^ ('c' - 'z')].reduce should contain only ('a' -> 'b'))
        "'a' - 'z' &^ ('A' - 'D')" in (Reduce['a' - 'z' &^ ('A' - 'D')].reduce should contain only ('a' -> 'z'))
      }
    }
  }
}
