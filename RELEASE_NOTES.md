# Release Notes

## 1.2

`Test`

- Added `elevatedAuthenticator` configuration (see [ExecuteMdx](./etc/doc/en/tasks/ExecuteMdx.md)).

- Added `timeout` configuration (default:`30s`) related to the REST API requests. Can be overridden both
  at actor and task levels.

- Added `load` configuration to perform some [load-testing](./etc/doc/en/LoadTestConfiguration.md).

`Tasks`

- Added `performanceTargets` configuration defining several targets related to performance.

- Added `ClearAuthDataCache` task to clear the data cached by the authentication/authorization service.

- Added `ClearOnTheFlyPermsCache` task to Clear the cache containing the created on-the-fly permissions.

`Task:MDXes`

- Added `shuffle` configuration to randomly permutes the list of MDX statements before executing them.

- Added `tidyMaxRowCount` configuration to limit the number of rows returned.

`Task:ExecuteMdx`

- Renamed its JSON5 action ID from `MDX` to `ExecuteMDX` for consistency purpose.

- Added `tidyMaxRowCount` configuration to limit the number of rows returned.

- The assertions can use the `elevatedAuthenticator` of the test.

`Task:OpenReport`

- The task can now generate the results of the MDX statements sent to the server.

- The task can now assert the results of the MDX statements sent to the server using expected results.

## 1.1

`Test`

- Added JSON validation.

- Added `duration` configuration and parallel actor execution : each actor runs in its own thread of control.

- Added `chrome` configuration to use Chrome or Chromium for the `OpenReport` task.

`Actor`

- Added `dumpJson` and `dumpResult` configuration.

- Authentication : support for HTTP headers authorization.

`Tasks`

- Added `DeleteSchemaBackup` task.

- Added a way to pass information between tasks (e.g., timestamp of a generated schema backup).

- Added `dumpJson` and `dumpResult` configuration (overriding actor's values).

- Added `pause` configuration to wait (possibly randomly) after the execution of a task.

`Task:MDXes`

- Added `pauses` configuration to wait (possibly randomly) after the execution of each MDX statement.

`Task:OpenReport`

- Added support for both FORM and HTTP headers authentication.

- Added support to extract MDX statements.

## 1.0

`General`

- Initial release of the project introducing several test building blocks.

_