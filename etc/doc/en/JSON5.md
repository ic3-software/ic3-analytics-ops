# JSON5

This project is using [JSON5](https://json5.org/) file format for its configuration file (e.g., test).
JSON5 is an extension of the JSON file format that aims to be easier to write and maintain by hand.

## Extended JSON Syntax

This project supports the following JSON5 features via the lenient mode of the Google `Gson` parser.

```json5
{
  // comments
  unquoted: "and you can quote me on that",
  singleQuotes: 'I can use "double quotes" here',
  lineBreaks: "Look, Mom! \
No \\n's!",
  "backwardsCompatible": "with JSON",
}
```

## Java System Properties

The file can contain references to Java system properties defined at runtime.

For example, the following `authenticator` definition :

```json5
{
  authenticator: {
    user: "${analytics.ops.user}",
    password: "${analytics.ops.password}"
  }
}
```

is using two Java system properties to avoid storing credentials in the configuration file itself :

- `analytics.ops.user`
- `analytics.ops.password`

_