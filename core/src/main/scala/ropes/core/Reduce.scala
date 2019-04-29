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

import Spec._

trait Reduce[S <: Spec] {

  /**
    * @return A inclusive range of characters which match the spec. The second character in each tuple must be
    *         greater than or equal to the first. The list must be in ascending order of first character in each tuple.
    */
  def reduce: List[(Char, Char)]
}

object Reduce {
  def apply[S <: Spec](implicit s: Reduce[S]): Reduce[S]    = s
  def instance[S <: Spec](r: (Char, Char)): Reduce[S]       = new Reduce[S] { def reduce = List(r) }
  def instance[S <: Spec](r: List[(Char, Char)]): Reduce[S] = new Reduce[S] { def reduce = r }

  implicit def `reduce==`[V <: Char with Singleton](implicit v: ValueOf[V]): Reduce[==[V]] =
    instance(v.value -> v.value)

  implicit def `reduce||`[L <: Nestable, R <: Nestable](implicit left: Reduce[L], right: Reduce[R]): Reduce[L || R] = {
    val all = left.reduce ::: right.reduce
    instance(
      mergeAll(all)
    )
  }

  private def mergeAll(all: List[(Char, Char)]) = {
    all.sortBy(_._1).foldLeft(List.empty[(Char, Char)]) {
      case (List(), first) => List(first)
      case (merged, next) =>
        val value = merged.init ::: merge(merged.last, next)
        println(all.sortBy(_._1) -> value)
        value
    }
  }

  private def merge(l: (Char, Char), r: (Char, Char)): List[(Char, Char)] = {
    val (l1, _) = l
    val (r1, _) = r

    val (s @ (s1, s2), b @ (b1, b2)) = if (l1 <= r1) (l, r) else (r, l)
    if (s2 + 1 >= b1.toInt) List(s1 -> b2)
    else List(s, b)
  }
  implicit def `reduce-`[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Reduce[Start - End] = {
    if (start.value <= end.value) instance(start.value -> end.value)
    else throw new IllegalStateException(s"""Range "'${start.value}' - '${end.value}'" is invalid""")
  }

  implicit val `reduce*` : Reduce[*] = instance(Char.MinValue -> Char.MaxValue)

  implicit def `reduce&^`[L <: Spec, R <: Nestable](implicit left: Reduce[L], right: Reduce[R]): Reduce[L &^ R] = {
    val l = left.reduce
    val r = right.reduce
    val removed = r.foldLeft(l) {
      case (previous, a) =>
        val value = previous.flatMap(removeRight(_, a))
        println("remove" -> a -> previous -> value)
        value
    }
    instance(mergeAll(removed))
  }

  private def removeRight(l: (Char, Char), r: (Char, Char)) = {
    val (l1, l2) = l
    val (r1, r2) = r

    if (r1 <= l1 && l2 <= r2) List.empty // right completely overlaps left
    else if (l2 < r1 || r2 < l1) List(l) // left and right do not intersect
    else
      List(
        Option.when(l._1 < r._1)(l._1              -> (r._1 - 1).toChar),
        Option.when(r._2 < l._2)((r._2 + 1).toChar -> l._2)
      ).flatten
  }
}
