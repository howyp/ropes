package integration

import org.scalatest.{FreeSpec, Matchers}

import ropes._

class ExamplesSpec extends FreeSpec with Matchers {
  "Some examples of valid ropes include" - {
    //This is very simplified - starts with an '@', then any characters
    type TwitterHandle = Concat[Exactly['@'], AnyString]
    "twitter handles" in {
      val Parse.Result.Complete(parsed) = Rope.parseTo[TwitterHandle]("@howyp")
      parsed.suffix.value should be("howyp")
    }
  }
}