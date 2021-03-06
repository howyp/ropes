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

<!--- What about setting sections? --->