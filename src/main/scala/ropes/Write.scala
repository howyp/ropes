package ropes

trait Write[R <: Rope] {
  def write(r: R): String
}
