# ClearResultCache

Clear the data cached by the authentication/authorization service. A service implementation might need to access
for example an external DB server to create the actual role definition. This request allows for clearing/invalidating
any cached data.

## JSON Definition

```typescript
interface ClearAuthDataCache extends Task {
}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

```json5
{
  action: "ClearAuthDataCache",
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/server/AOClearAuthDataCacheTask.java).

_
