# ChromeConfiguration

Define the location of the Chrome or Chromium executable to use with the [OpenReport](tasks/OpenReport.md) task.

## JSON Definition

```typescript
interface ChromeConfiguration {

    // Reference the absolute path of the executable (Chrome or Chromium) to launch. 
    // When not defined, the shell is looking for Chrome.
    exec?: string;

    // Optional extra. command line arguments for the Chrome|Chromium process.
    execOptions?: string;

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Test`](./Test.md)

## Example

Using `Chromium` instead of `Chrome` (default) :

```json5
{
  chrome: {
    exec: "chromium",
  }
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/task/reporting/AOChromeConfiguration.java).

_