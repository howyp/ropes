---
layout: home
---
#### Type-level String Formats

Declare your string formats at the type level, and
get parsers, writers, composition and generators for free.

### Introduction

Everyone has come across code before that is *string*ly typed - where
there are strong types for data structures, but fields that we know
**must** match a certain format are represented simply as `String`s.
Things like email addresses, host names, post codes, user/product IDs
etc. And often we don't just need to validate the formats, but to be
able to decompose and use specific parts of them

So, the aim of Ropes is to give a general way to define such
strongly-typed strings, and give many useful ways to work with them.

### Principles

_The type of a `Rope` completely expresses the definition of what values
are valid._

Every valid `Rope` can be:
* **Parsed** safely from a `String`
* **Written** precisely back to a `String`
- **Decomposed** into its constituent parts
- **Composed** with other `Rope`s to form a new ones
- **Generated** through arbitrary values which always adhere to the
  definition

### Current Status

At the moment, this is very much an alpha. Although lots of effort has
been made to ensure good testing, the API may be subject to breaking and
radical changes.

Also, because it supports
[literal types](https://docs.scala-lang.org/sips/42.type.html), for the
moment we only support Scala 2.13, but it should be possible to also
build against Scala 2.12 in the future.

### Inspirations

Frank Thomas's **excellent**
[refined](https://github.com/fthomas/refined) library provides a way to enforce
restrictions on a wide range of value and collection types in Scala,
including `String`s. It does this through predicates, which give
great power to the user to define any refinement they choose.

However, because of this power, it is also problematic to provide the
ability to decompose a refined `String` based on the given predicate,
and is also not generally possible to generate arbitrary values matching
predicates.

Getting Started
===

Let's dive straight into some examples.

### A Basic Rope: Twitter Handles

We know that (simplistically) a Twitter handle always starts with a
literal '@' symbol, followed by any string. We can define that with:

```tut:silent
import ropes.core._

type TwitterHandle = Concat[Literal['@'], AnyString]
```

Or you can use Concat as an infix type:

```tut:silent
type TwitterHandle = Literal['@'] Concat AnyString
```

Now, we can parse matching strings, and write them back to their
original form:

```tut:book
Rope.parseTo[TwitterHandle]("!Bob")
val Right(howy) = Rope.parseTo[TwitterHandle]("@HowyP")
howy.write
```

#### Composing and decomposing

After parsing, we can access the parts of the rope based on the
properties of each individual type. In this case, they are pretty basic.
A `Concat` has a prefix and suffix, and the `Literal` and `AnyString`
contain single values:

```tut:book
howy.prefix
howy.prefix.value
howy.suffix
howy.suffix.value
```

We can also create new handles from scratch, or modify existing ones:

```tut:book
val twitter: TwitterHandle = Concat(Literal('@'), AnyString("Twitter"))
val bob: TwitterHandle     = howy.copy(suffix = AnyString("Bob"))
```

#### Generating

Lastly, a feature of all `Rope`s is that they can be generated via
Scalacheck `Arbitrary`:

```tut:book
import org.scalacheck.Arbitrary.arbitrary
import ropes.scalacheck._

List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\n')
```

#### Restricting with `Repeated` and `Letter`

But wait! Those handles don't look very realistic. Only 15 letters are
allowed for the username portion of the handle. Let's update our
specification:

```tut:silent
type Username      = Repeated[1, 15, Letter]
type TwitterHandle = Literal['@'] Concat Username
```

Being more precise, we've stated that the `Username` must consist of
letter characters, repeated 1 to 15 times. It now will not allow
usernames which are too long or have non-letter characters:

```tut:book
Rope.parseTo[TwitterHandle]("@HowyP")
Rope.parseTo[TwitterHandle]("@TwoManyCharactersForAUsername")
Rope.parseTo[TwitterHandle]("@foo&bar")
```

Now, let's try generating some handles again:

```tut:book
List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\n')
```

#### Defining character sets with `Range`

`Letter` comes pre-defined in ropes as:

```tut:silent
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

### A Tougher Rope: Social Security Numbers

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

#### Naming sections with `Named`

If we'd like, we can also access sections by giving them a name:

```tut:silent
type Area   = Repeated.Exactly[3, Digit] WithName "Area"
type Group  = Repeated.Exactly[2, Digit] WithName "Group"
type Serial = Repeated.Exactly[4, Digit] WithName "Serial"
```

```tut:invisible
type SSN = Area +: Dash +: Group +: Dash +: Serial
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
```

```tut:book
parsed.section["Area"].write
parsed.section["Group"].write
parsed.section["Serial"].write
```

#### Representing sections as `Int`s with `ConvertedTo`

When we've accessed the area, group and serial numbers above, we've used
`.write` to returned them as `String`s. That's because the actual return
type is `Repeated.Exactly[N, Digit]`, which gives it's values as a list
of `Int`s. That's a bit difficult to work with, and they'd be more
naturally represented as `Int`s.

Ropes provides a way to do this using `ConvertedTo`, which takes a rope
type and the type you want to convert to and from:

```tut:silent
type Area   = Repeated.Exactly[3, Digit] ConvertedTo Int WithName "Area"
type Group  = Repeated.Exactly[2, Digit] ConvertedTo Int WithName "Group"
type Serial = Repeated.Exactly[4, Digit] ConvertedTo Int WithName "Serial"
```

```tut:invisible
type SSN = Area +: Dash +: Group +: Dash +: Serial
val Right(parsed) = Rope.parseTo[SSN]("078-05-1120")
```

We can now use `.value` on each section and get a simple `Int`:

```tut:book
parsed.section["Area"].value
parsed.section["Group"].value
parsed.section["Serial"].value
```

<!--- Explain `Conversion` --->

<!--- What about setting sections? --->