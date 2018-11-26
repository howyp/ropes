package ropes.instances
import ropes.{Conversion, ConvertedTo, Parse, Rope, Write}

trait ConvertedToInstances {
  implicit def convertedToParse[Source <: Rope, Target](
      implicit sourceParse: Parse[Source],
      conversion: Conversion[Source, Target]): Parse[Source ConvertedTo Target] =
    sourceParse.parse(_).flatMap { (source, remainder) =>
      Parse.Result.Success(ConvertedTo[Source, Target](conversion.convert(source)), remainder)
    }

  implicit def convertedToWrite[Source <: Rope, Target](
      implicit sourceWrite: Write[Source],
      conversion: Conversion[Source, Target]): Write[Source ConvertedTo Target] =
    target => sourceWrite.write(conversion.convert(target.value))
}
