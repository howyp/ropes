Ropes
=====

Stronger strings!

Declare your string format as a type, and get parsers, writers,
composition and generators for free.

### Motivation

Everyone has come across code before that is *string*ly typed - where there
are strong types for data structures, but fields that we know **must**
match a certain format are represented simply as `String`s. Example of
these formats are email addresses, host names, national insurance
numbers, parcel tracking identifiers, twitter usernames.

While it is quite possible to write custom value types to ensure such
formats are adhered to, it can be time consuming and involve a lot of
boiler-plate.

The goal of this library is to give a general way to define such
strongly-typed strings, with some added benefits in the process.

### Principles

_The type of a `Rope` completely expresses the definition of what values
are valid._

Every valid `Rope` can be:
* **Parsed** safely from a `String`
* **Writen** precisely back to a `String`
- **Decomposed** into its constituent parts
- **Composed** with other `Rope`s to form a new one
- **Generate** arbitrary values which match the definition

### Current Status

At the moment, this is very much a proof of concept to see if we can
get something working that is useful more than a toy.

Because it supports
[literal types](https://docs.scala-lang.org/sips/42.type.html), for the
 moment we only support Scala 2.13, but it should be possible to also
 build against Scala 2.12 in the future

### Inspirations

Frank Thomas's **excellent**
[refined](https://github.com/fthomas/refined) library provides a way to enforce
restrictions on a wide range of value and collection types in Scala,
including `String`s. It does this through predicates, which give
great power to the user to define any refinement they choose.

However, because of this power, it is also problematic to provide
the ability to decompose a refined `String` based on the given
predicate, and is also not generally possible to generate arbitrary
values matching predicates. By restricting the types and