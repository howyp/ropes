---
layout: docs
title: 3. Concats
---

# Section 3 - Handing Concats - Social Security Numbers

According to
[Wikipedia](https://en.wikipedia.org/wiki/Social_Security_number#Structure)
a US Social Security number is:

>a nine-digit number in the format "AAA-GG-SSSS". The number is divided 
>into three parts: the first three
>digits, known as the area number because they were formerly assigned by
>geographical region; the middle two digits, known as the group number;
>and the final four digits, known as the serial number.

So let's build that as a `Rope`:

```tut:silent
import ropes.core._

type Area   = Repeated.Exactly[3, Digit]
type Group  = Repeated.Exactly[2, Digit]
type Serial = Repeated.Exactly[4, Digit]
type Dash   = Literal['-']
type SSN    = Concat[Area, Concat[Dash, Concat[Group, Concat[Dash, Serial]]]]
```

We're using two new types here. `Digit` defines a numeric character 
between 0 and 9. `Repeated.Exactly[N, R]` specifies that we expect `N` 
instances of the rope `R`.

#### Working with nested `Concat`s

The definition for `SSN` we have so far isn't very easy to read because
of all the nesting. We can make it simpler by using the `+:` syntax from
the DSL:

```tut:silent
import ropes.dsl._
type SSN = Area +: Dash +: Group +: Dash +: Serial
```
#### Using `section`

We can parse and access parts of the SSN in the same way as for the
twitter handle:

```tut:book
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
parsed.prefix.write
parsed.suffix.suffix.prefix.write
parsed.suffix.suffix.suffix.suffix.write
```

but it is clumsy to navigate through all of the prefixes and suffixes.
Instead, we can use the `section` method to access a given section by
index:

```tut:book
parsed.section[1].write
parsed.section[3].write
parsed.section[5].write
```

