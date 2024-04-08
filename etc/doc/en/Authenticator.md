# Authenticator

An authenticator allows for defining the icCube credentials used by an [actor](./Actor.md). It can be defined
both in the test and in the actor (overriding the value possibly defined in the test).

## JSON Definition

```typescript
interface Authenticator {
    
    user : string;
    
    password: string;
}
```

See also :

- [`JSON5`](./JSON5.md)

## Example

Remember that the `JSON5` definition can contain references to **Java system properties** defined at runtime
to avoid storing credentials in the configuration file itself :

```json5
{
  authenticator: {
    user: "${analytics.ops.user}",
    password: "${analytics.ops.password}"
  }
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOAuthenticator.java).

_