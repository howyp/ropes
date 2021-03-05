---
layout: docs
title: 4. The Ropes DSL
---

```scala mdoc:invisible
import ropes.core._

type Area   = Repeated.Exactly[3, Range['0', '9']]
type Group  = Repeated.Exactly[2, Range['0', '9']]
type Serial = Repeated.Exactly[4, Range['0', '9']]
type Dash   = Literal['-']
```

# Section 4 - The Ropes DSL

The definition for `SSN` we have so far isn't very easy to read because
of all the nesting of `Concats`. The DSL module provides symbolic
operations to make this sort of thing simpler. 

Using it, we can re-write our definition using the `+:` syntax from the
DSL:

```scala mdoc:silent
import ropes.dsl._
type SSN = Area +: Dash +: Group +: Dash +: Serial
```
