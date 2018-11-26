package ropes

trait Conversion[Source <: Rope, Target] {
  //TODO could this be a total function - ie. non-optional return type?
  def convert(source: Source): Option[Target]
  def convert(target: Target): Source
}
