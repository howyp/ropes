---
layout: docs
title: 5. Naming
---

# Section 5 - Naming sections with `Named`

If we'd like, we can name parts of our rope:

```scala mdoc:silent
import ropes.core._

type Area   = Repeated.Exactly[3, Digit] Named "Area"
type Group  = Repeated.Exactly[2, Digit] Named "Group"
type Serial = Repeated.Exactly[4, Digit] Named "Serial"
```

```scala mdoc:invisible
import ropes.dsl._
type SSN = Area +: Literal['-'] +: Group +: Literal['-'] +: Serial
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
```

This makes sections easy to acess:

```scala mdoc
parsed.section["Area"].write
parsed.section["Group"].write
parsed.section["Serial"].write
```

<!--Adding/removing names. Setting names on ropes.-->
