---
layout: docs
title: 3. Concats
---

# Section 3 - Handing Multiple Concats - Social Security Numbers

According to
[Wikipedia](https://en.wikipedia.org/wiki/Social_Security_number#Structure)
a US Social Security number is:

>a nine-digit number in the format "AAA-GG-SSSS". The number is divided 
>into three parts: the first three
>digits, known as the area number because they were formerly assigned by
>geographical region; the middle two digits, known as the group number;
>and the final four digits, known as the serial number.

So let's build that as a `Rope`:

```scala mdoc:silent
import ropes.core._

type Area   = Repeated.Exactly[3, Range['0', '9']]
type Group  = Repeated.Exactly[2, Range['0', '9']]
type Serial = Repeated.Exactly[4, Range['0', '9']]
type Dash   = Literal['-']
type SSN    = Concat[Area, Concat[Dash, Concat[Group, Concat[Dash, Serial]]]]
```

The `Repeated.Exactly[N, R]` is just an alias for `Repeated` which uses
the same value for maximum and minimum instances.

To allow multiple concatenations, the nesting *must occur on the suffix*
rather than the prefix.

#### Using `section`

We _could_ access parts of the SSN in the same way as we have done
previously:

```scala mdoc
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
parsed.prefix.write
parsed.suffix.suffix.prefix.write
parsed.suffix.suffix.suffix.suffix.write
```

but it is clumsy to navigate through all of the prefixes and suffixes.
Instead, we ropes provides the `section` method to access a given
section by index:

```scala mdoc
parsed.section[1].write
parsed.section[3].write
parsed.section[5].write
```

