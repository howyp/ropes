package ropes.instances
import ropes.{Conversion, Range}

trait DigitInstances {
  //TODO consider if we can write Conversion[Digit]
  implicit val digitConversion: Conversion[Range['0', '9'], Int] = Conversion(
    forwards = _.value.toInt - '0'.charValue(),
    backwards = target => Range.unsafeFrom((target + '0'.charValue()).toChar)
  )
}
