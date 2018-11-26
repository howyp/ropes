package ropes

trait Conversion[Source <: Rope, Target] {
  def convert(source: Source): Target
  def convert(target: Target): Source
}
