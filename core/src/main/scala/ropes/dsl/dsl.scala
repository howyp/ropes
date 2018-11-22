package ropes

package object dsl {
  type +[Prefix <: Rope, Suffix <: Rope] = Concat[Prefix, Suffix]

  implicit class ConcatOps[Prefix <: Rope](prefix: Prefix) {
    def +[Suffix <: Rope](suffix: Suffix): Concat[Prefix, Suffix] = Concat(prefix, suffix)
  }
}
