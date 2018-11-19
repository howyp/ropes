package ropes.instances

import ropes.{AnyString, Parse, Write}

trait AnyStringInstances {
  implicit val anyStringParse: Parse[AnyString] = str => Parse.Result.Complete(AnyString(str))
  implicit val anyStringWrite: Write[AnyString] = _.value
}