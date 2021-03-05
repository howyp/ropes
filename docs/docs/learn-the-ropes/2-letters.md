---
layout: docs
title: 2. Letters
---

# Section 2 - Letters

#### Restricting with `Repeated` and `Letter`

But wait! The handles we made in section 1 don't look very realistic.
Only 15 letters are allowed for the username portion of the handle.
Let's update our specification:

```scala mdoc:silent
import ropes.core._

type Username      = Repeated[1, 15, Letter]
type TwitterHandle = Literal['@'] Concat Username
```

Being more precise, we've stated that the `Username` must consist of
letter characters, repeated 1 to 15 times. It now will not allow
usernames which are too long or have non-letter characters:

```scala mdoc
Rope.parseTo[TwitterHandle]("@HowyP")
Rope.parseTo[TwitterHandle]("@TwoManyCharactersForAUsername")
Rope.parseTo[TwitterHandle]("@foo&bar")
```

Now, let's try generating some handles again:

```scala mdoc
import org.scalacheck.Arbitrary.arbitrary
import ropes.scalacheck._

List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\n')
```

#### Restricting allowed characters with `Range`

`Letter` comes pre-defined in ropes as:

```scala mdoc:silent
type Letter = Letter.Uppercase Or Letter.Lowercase
object Letter {
    type Uppercase = Range['A', 'Z']
    type Lowercase = Range['a', 'z']
}
```

It defines the upper and lower case characters using a `Range`, which
takes two literal type parameters specifying the minimum and maximum
characters allowed. `Or` lets us to join the two, allowing characters
from either range.
