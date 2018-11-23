package ropes.instances

import ropes._

trait ConcatInstances {
  implicit def concatParse[P <: Rope: Parse, S <: Rope: Parse]: Parse[Concat[P, S]] = { str =>
    val (prefix, remaining) = Rope.parseTo[P](str) match {
      case Parse.Result.Incomplete(prefix, remaining) => (prefix, remaining)
      case Parse.Result.Complete(prefix)              => (prefix, "")
    }
    Rope.parseTo[S](remaining) match {
      case Parse.Result.Incomplete(suffix, remaining) => Parse.Result.Success(Concat(prefix, suffix), remaining)
      case Parse.Result.Complete(suffix)              => Parse.Result.Complete(Concat(prefix, suffix))
    }
  }

  implicit def concatWrite[P <: Rope: Write, S <: Rope: Write]: Write[Concat[P, S]] =
    concat => concat.prefix.write + concat.suffix.write
}
