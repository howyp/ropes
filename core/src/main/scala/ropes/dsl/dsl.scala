package ropes

package object dsl {
  //TODO Perhaps this should be specified next to the types itself? I guess this comes down to whether we want a
  //separation between the main types and a DSL, or the main types and the _ops_
  type :+[Prefix <: Rope, Suffix <: Rope] = Concat[Prefix, Suffix]
  val :+ = Concat

  implicit class RopeOps[Prefix <: Rope](prefix: Prefix) {
    def :+[Suffix <: Rope](suffix: Suffix): Concat[Prefix, Suffix] = Concat(prefix, suffix)
  }

  implicit class LiteralOpsViaExactly[Literal <: Char with Singleton](literal: Literal)(implicit v: ValueOf[Literal])
      extends RopeOps(prefix = Exactly[Literal](literal))
}
