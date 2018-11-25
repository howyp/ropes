package ropes.instances

import ropes.{Range, Parse, Write}

trait RangeInstances {
  implicit def rangeParse[Start <: Char with Singleton, End <: Char with Singleton](
      implicit start: ValueOf[Start],
      end: ValueOf[End]): Parse[Range[Start, End]] = { str =>
    if (str.length > 0)
      Range.from[Start, End](str.charAt(0)).map(Parse.Result.Complete(_)).getOrElse(Parse.Result.Failure)
    else Parse.Result.Failure
  }

  implicit def rangeWrite[Start <: Char with Singleton, End <: Char with Singleton]: Write[Range[Start, End]] =
    _.value.toString
}
