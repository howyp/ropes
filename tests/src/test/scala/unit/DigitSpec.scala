package unit
import laws.RopeLaws
import ropes._

class DigitSpec extends RopeLaws {
  "A Digit" in {
    val Parse.Result.Complete(digit) = Rope.parseTo[Digit]("1")
    digit.value should be(1)
    digit.write should be("1")
  }
}
