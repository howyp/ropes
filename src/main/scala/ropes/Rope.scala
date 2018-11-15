package ropes


sealed trait Rope {
  def write: String
}
object Rope {
  def parseTo[R <: Rope](s: String): Parse.Result[R] = ???

  //TODO find a monad for the return type
  def generateArbitrary[R <: Rope]: Iterator[R] = ???
}