# Shell

The shell represents the main entry point for running a [test](./Test.md). It stops as soon as an error is generated
and exits the process on error (i.e., `exit(-1)`). Without any error, the process exits on success (i.e., `exit(0)`).

## Run

The shell is running a single [test](./Test.md) instance whose `JSON5` configuration is defined via the
Java system property `analytics.ops.test`.

### .env.dev

Optionally, the shell is first checking for the `.env.dev` file in the current working directory defining several
Java system properties :

```
analytics.ops.user     : admin
analytics.ops.password : admin
analytics.ops.test     : /opt/tests/stress-testing.json5
```

### AnalyticsOps.sh

Once built, the script [`bin/AnalyticsOps.sh`](../../bin/AnalyticsOps.sh) allows for running the shell.

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/shell/AOShell.java).

_