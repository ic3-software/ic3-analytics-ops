# Authenticator

An authenticator allows for defining the icCube credentials used by an [actor](./Actor.md). It can be defined both
in the test and in the actor (overriding the value possibly defined in the test). It is either defined as a username
and password or as a list of HTTP headers for [embedded](https://doc.iccube.com/?ic3topic=devops.Embedded) scenarios.

## JSON Definition

```typescript
interface Authenticator {

    user?: string;

    password?: string;

    headers?: Header[];

}

interface Header {

    name: string;

    value: string;

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

Connecting to an icCube server as embedded into an existing Web application and configured to retrieve
the authorization profile from HTTP headers :

```json5
{
  authenticator: {
    headers: [
      {
        name: "IC3_USER_NAME",
        value: "${analytics.ops.IC3_USER_NAME}"
      },
      {
        name: "IC3_ROLE_NAME",
        value: "${analytics.ops.IC3_ROLE_NAME}"
      }
    ]
  }
}
```
## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOAuthenticator.java).

_