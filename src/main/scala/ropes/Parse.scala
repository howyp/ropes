package ropes

object Parse {
  type Result[R <: Rope] = Either[Parse.Failure, R]
  //TODO this should not be string-based but contain position/expected/actual
  final case class Failure(message: String)
}
