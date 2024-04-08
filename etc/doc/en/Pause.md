# Pause

A pause represents a time-based amount of time (e.g., 30 seconds) defined as a string. The pause can be either
a fixed amount of time or a random amount between a minimum and maximum values (both inclusive).

**Fixed**

The pause is defined using a [duration](./Duration.md).

**Random**

The pause is defined using two [durations](./Duration.md) (min. and max. values) separated by the `:` character.

## JSON Definition

```typescript
type Pause = string;
```

See also :

- [`JSON5`](./JSON5.md)
- [`Duration`](./Duration.md)

## Examples

```
"PT5s"         : parses as a fixed pause of 5 seconds
"PT1s:PT10s"   : parses as a random pause between 1 and 10 seconds
"PT0.5s:PT1s"  : parses as a random pause between 500 milliseconds and 1 second
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/common/AOPause.java).

_