package ropes

trait Parse[R <: Rope] {
  def parse(string: String): Parse.Result[R]
}
object Parse {

  sealed trait Result[+R <: Rope] {
    def flatMap[S <: Rope](f: (R, String) => Result[S]): Result[S] = this match {
      case Result.Failure          => Result.Failure
      case Result.Complete(v)      => f(v, "")
      case Result.Incomplete(v, r) => f(v, r)
    }
  }
  object Result {
    case object Failure             extends Result[Nothing]
    sealed trait Success[R <: Rope] extends Result[R]

    object Success {
      def apply[R <: Rope](value: R, remaining: String): Success[R] = remaining match {
        case ""       => Complete(value)
        case nonEmpty => new Incomplete(value, nonEmpty) {}
      }
    }

    final case class Complete[R <: Rope](value: R)                                extends Success[R]
    sealed abstract case class Incomplete[R <: Rope](value: R, remaining: String) extends Success[R]
  }
}
