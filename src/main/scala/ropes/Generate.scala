package ropes

trait Generate[R <: Rope] {
  def generate: Iterator[R]
}
