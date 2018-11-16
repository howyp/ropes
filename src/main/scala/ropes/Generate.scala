package ropes

trait Generate[R <: Rope] {
  //TODO find a better monad for the return type
  def generate: Iterator[R]
}
