---
layout: docs
title: 6. Conversions
---

# Section 6 - Conversions

When we've accessed the area, group and serial numbers in previous
sections, we've used `.write` to return them as `String`s. That's
because the actual return type is `Repeated.Exactly[N, Digit]`, which
gives it's values as a list of `Int`s. That's a bit difficult to work
with, and they'd be more naturally represented as `Int`s.

<!--TODO Describe `Digit`-->

Ropes provides a way to do this using `ConvertedTo`, which takes a rope
type and the type you want to convert to and from:

```scala mdoc:silent
import ropes.core._

type Area   = Repeated.Exactly[3, Digit] ConvertedTo Int Named "Area"
type Group  = Repeated.Exactly[2, Digit] ConvertedTo Int Named "Group"
type Serial = Repeated.Exactly[4, Digit] ConvertedTo Int Named "Serial"
```

```scala mdoc:invisible
import ropes.dsl._
type SSN = Area +: Literal['-'] +: Group +: Literal['-'] +: Serial
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
```

We can now use `.value` on each section and get a simple `Int`:

```scala mdoc
parsed.section["Area"].value
parsed.section["Group"].value
parsed.section["Serial"].value
```

<!--- Explain `Conversion` --->
