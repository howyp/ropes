package ropes.instances

import ropes._

trait ConcatInstances {
  implicit def concatParse[P <: Rope: Parse, S <: Rope: Parse]: Parse[Concat[P, S]] = { str =>
    Rope.parseTo[P](str).flatMap {
      case (prefix, afterSuffix) =>
        Rope.parseTo[S](afterSuffix).flatMap {
          case (suffix, remaining) => Parse.Result.Success(Concat(prefix, suffix), remaining)
        }
    }
  }

  implicit def concatWrite[P <: Rope: Write, S <: Rope: Write]: Write[Concat[P, S]] =
    concat => concat.prefix.write + concat.suffix.write
}
