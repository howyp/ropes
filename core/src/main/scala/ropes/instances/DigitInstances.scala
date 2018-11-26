package ropes.instances
import ropes.{Conversion, Range}

trait DigitInstances {
  //TODO consider if we can write Conversion[Digit]
  implicit val digitConversion: Conversion[Range['0', '9'], Int] = new Conversion[Range['0', '9'], Int] {
    def convert(source: Range['0', '9']) = source.value.toInt - '0'.charValue()
    def convert(target: Int)             = Range.unsafeFrom((target + '0'.charValue()).toChar)
  }
}
