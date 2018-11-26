package ropes.instances

import ropes.{Range, Parse, Write}

private[ropes] trait RangeInstances {
  implicit def rangeParse[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Parse[Range[Start, End]] = { str =>
    str.headOption
      .flatMap(Range.from[Start, End](_))
      .map(Parse.Result.Success(_, str.tail))
      .getOrElse(Parse.Result.Failure)
  }

  implicit def rangeWrite[Start <: Char with Singleton, End <: Char with Singleton]: Write[Range[Start, End]] =
    _.value.toString
}
