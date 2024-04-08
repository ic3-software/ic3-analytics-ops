# Duration

A duration represents a time-based amount of time (e.g., 30 seconds) defined as a string. The formats accepted
are based on the ISO-8601 duration format `PnDTnHnMn.nS` with days considered to be exactly 24 hours.

## JSON Definition

```typescript
type Duration = string;
```

See also :

- [`JSON5`](./JSON5.md)

The string starts with the ASCII letter `P` in upper or lower case. There are then four sections, each consisting
of a number and a suffix. The sections have suffixes in ASCII of `D`, `H`, `M` and `S` for days, hours, minutes
and seconds, accepted in upper or lower case. The suffixes must occur in order. The ASCII letter `T` must occur
before the first occurrence, if any, of an hour, minute or second section. At least one of the four sections must
be present, and if `T` is present there must be at least one section after the `T`. The number part of each section
must consist of one or more ASCII digits. The number of days, hours and minutes must parse to a long. The number of
seconds must parse to a long with optional fraction. The decimal point may be either a dot or a comma. The fractional
part may have from zero to 9 digits.

## Examples

```
"PT20.345s" : parses as "20.345 seconds"
"PT15m"     : parses as "15 minutes" (where a minute is 60 seconds)
"PT10h"     : parses as "10 hours" (where an hour is 3600 seconds)
"P2D"       : parses as "2 days" (where a day is 24 hours or 86400 seconds)
"P2dT3h4m"  : parses as "2 days, 3 hours and 4 minutes"
```

_