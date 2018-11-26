package ropes.instances
import ropes.{Conversion, ConvertedTo, Parse, Rope}

trait ConvertedToInstances {
  implicit def convertedToParse[Source <: Rope, Target](
      implicit sourceParse: Parse[Source],
      conversion: Conversion[Source, Target]): Parse[Source ConvertedTo Target] =
    sourceParse.parse(_).flatMap { (source, remainder) =>
      conversion.convert(source) match {
        case Some(target) => Parse.Result.Success(ConvertedTo[Source, Target](target), remainder)
      }
    }
}
