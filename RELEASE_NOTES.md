# Release Notes

## 1.2 ( next-version )

`Tasks`

- Added `performanceTargets` configuration defining several targets related to performance.

`Task:MDXes`

- Added `shuffle` configuration to randomly permutes the list of MDX statements before executing them.

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