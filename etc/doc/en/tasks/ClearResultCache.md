# ClearResultCache

Clear the MDX result cache used for the MDX queries sent by the dashboards application. This can be used to ensure
that the `OpenReport` and `PrintReport` tasks are not using MDX results from the server cache.

## JSON Definition

```typescript
interface ClearResultCacheTask extends Task {
}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

```json5
{
  action: "ClearResultCache",
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/server/AOClearResultCacheTask.java).

_
