package ropes.instances

import ropes.{Exactly, Parse, Write}

trait ExactlyInstances {
  implicit def exactlyParseChar[C <: Char with Singleton](implicit c: ValueOf[C]): Parse[Exactly[C]] = { str =>
    if (str.length == 1 && str.charAt(0) == c.value) Parse.Result.Complete(Exactly[C](c.value))
    else if (str.length > 0 && str.charAt(0) == c.value) Parse.Result.Incomplete(Exactly[C](c.value), str.substring(1))
    else Parse.Result.Failure
  }
  implicit def exactlyWriteChar[C <: Char with Singleton]: Write[Exactly[C]] = _.value.toString
}
