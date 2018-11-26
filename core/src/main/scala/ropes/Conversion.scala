package ropes

trait Conversion[Source <: Rope, Target] {
  def forwards(source: Source): Target
  def backwards(target: Target): Source
}
object Conversion {
  def apply[Source <: Rope, Target](forwards: Source => Target,
                                    backwards: Target => Source): Conversion[Source, Target] = {
    val f = forwards
    val b = backwards
    new Conversion[Source, Target] {
      def forwards(source: Source)  = f(source)
      def backwards(target: Target) = b(target)
    }
  }
}
