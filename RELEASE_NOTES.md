# Release Notes

## 1.1 ( next-version )

`Test`

- Added JSON validation.

- Added `duration` and parallel actor execution : each actor runs in its own thread of control.

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

## 1.0

`General`

- Initial release of the project introducing several test building blocks.

_