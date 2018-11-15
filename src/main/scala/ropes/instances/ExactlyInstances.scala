package ropes.instances

import ropes.{Exactly, Parse}

trait ExactlyInstances {
  implicit def exactlyParseChar[C <: Char](implicit c: ValueOf[C]): Parse[Exactly[C]] = { str =>
    if (str.length == 1 && str.charAt(0) == c.value) Parse.Result.Complete(Exactly[C](c.value))
    else if (str.length > 0 && str.charAt(0) == c.value) Parse.Result.Incomplete(Exactly[C](c.value), str.substring(1))
    else Parse.Result.Failure
  }
}
