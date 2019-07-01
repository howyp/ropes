---
layout: docs
title: 3. Character Classes
---

# Section 3 - Character Classes

Looking at the
[Twitter spec](https://help.twitter.com/en/managing-your-account/twitter-username-rules)
more closely, digits and `_` characters are also allowed in the username
portion of the handle. A simple `Range` will not be sufficient here, so
we can turn to `CharacterClass` and it's associated `Spec` types.

```tut:silent
import ropes.core._
import ropes.core.Spec._

type Username      = Repeated[1, 15, CharacterClass[('a' - 'z') || ('A' - 'Z') || ('0' - '9') || ==['_']]]
type TwitterHandle = Literal['@'] Concat Username
```

To keep things concise, it uses symbolic definitions, so let's at these
in turn:

* `'x' - 'y'` defines that any characters from `x` up to and including
  `y` are allowed
* `==['x']` defines that only character `x` is allowed
* `a || b` defines that characters matching the spec `a` or the spec `b`
  are allowed

