---
layout: docs
title: 1. Basics
---

# Section 1 - Basics

Let's dive straight into an example.

### Twitter Handles

We know that (simplistically) a Twitter handle always starts with a
literal '@' symbol, followed by any string. We can define that with:

```scala mdoc:silent
import ropes.core._

type TwitterHandle = Concat[Literal['@'], AnyString]
```

Or you can use `Concat` as an infix type:

```scala mdoc:silent:nest
type TwitterHandle = Literal['@'] Concat AnyString
```

Now, we can parse matching strings, and write them back to their
original form:

```scala mdoc
Rope.parseTo[TwitterHandle]("!Bob")
val Right(howy) = Rope.parseTo[TwitterHandle]("@HowyP")
howy.write
```

#### Composing and decomposing

After parsing, we can access the parts of the rope based on the
properties of each individual type. In this case, they are pretty basic.
A `Concat` has a prefix and suffix, and the `Literal` and `AnyString`
contain single values:

```scala mdoc
howy.prefix
howy.prefix.value
howy.suffix
howy.suffix.value
```

We can also create new handles from scratch, or modify existing ones:

```scala mdoc
val twitter: TwitterHandle = Concat(Literal('@'), AnyString("Twitter"))
val bob: TwitterHandle     = howy.copy(suffix = AnyString("Bob"))
```

#### Generating

Lastly, a feature of all `Rope`s is that they can be generated via
Scalacheck `Arbitrary`:

```scala mdoc
import org.scalacheck.Arbitrary.arbitrary
import ropes.scalacheck._

List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\n')
```