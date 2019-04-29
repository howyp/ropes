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

class SpecTest extends FreeSpec with Matchers {

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
        "==['a'] || ==['b'] || ==['c']" in (Reduce[==['a'] || ==['b'] || ==['c']].reduce should contain only ('a'        -> 'c'))
        "==['a'] || ==['c'] || ==['b']" in (Reduce[==['a'] || ==['c'] || ==['b']].reduce should contain only ('a'        -> 'c'))
        "==['c'] || ==['b'] || ==['a']" in (Reduce[==['c'] || ==['b'] || ==['a']].reduce should contain only ('a'        -> 'c'))
        "==['c'] || ==['d'] || ==['a']" in (Reduce[==['c'] || ==['d'] || ==['a']].reduce should contain inOrderOnly ('a' -> 'a', 'c' -> 'd'))
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
        "* &^ ==['c']" in (Reduce[* &^ ==['c']].reduce should contain inOrderOnly (Char.MinValue -> 'b', 'd' -> Char.MaxValue))
      }
      "* and a '-'" - {
        "* &^ ('c' - 'e')" in (Reduce[* &^ ('c' - 'e')].reduce should contain inOrderOnly (Char.MinValue -> 'b', 'f' -> Char.MaxValue))
      }
      "* and multiple literal chars" ignore {
        "* &^ (==['a'] || ==['g'])" in (Reduce[* &^ (==['a'] || ==['g'])].reduce should contain inOrderOnly (
          Char.MinValue -> '`',
          'b'           -> 'f',
          'h'           -> Char.MaxValue
        ))
      }
    }

    //[^a]
    type _3 = ('0' - 'Z') &^ ==['a']

    //[^abc]
    type _4 = * &^ (==['a'] || ==['b'] || ==['c'])

    //[a-zA-Z]
    type _5 = ('a' - 'z') || ('A' - 'Z')

    //[^a-zA-Z]
    type _6 = * &^ (('a' - 'z') || ('a' - 'z'))

    //[^abcA-Z]
    type _7 = * &^ (==['a'] || ==['b'] || ==['c'] || ('A' - 'Z'))

    //[abcA-Z,.;]
    type _8 = ==['a'] || ==['b'] || ==['c'] || ('A' - 'Z') || ==[','] || ==['.'] || ==[';']

    type _9 = ('a' - 'z') &^ ('h' - 'j')
  }
}
