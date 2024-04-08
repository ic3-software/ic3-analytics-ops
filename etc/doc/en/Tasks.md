# Tasks

A test is made of [actors](./Actor.md) that are running a list of [tasks](./Task.md). A task can load a schema,
open a dashboard, execute an MDX statement, etc... and can have one or more [assertions](./Assertion.md) to enforce
the expected results.

## Server Management

### [ServerStatus](./tasks/ServerStatus.md)

    Return some information about the icCube server (requires an icCube server v8.4.10 for the version info.).

### [ClearResultCache](./tasks/ClearResultCache.md)

    Clear the MDX result cache used for the MDX queries sent by the dashboards.

## Schema Management

### [LoadSchema](./tasks/LoadSchema.md)

    Fully (or incrementally) load a schema.

### [RestoreSchemaBackup](./tasks/RestoreSchemaBackup.md)

    Load a schema by restoring a backup.

### [RestoreSchemaSnapshot](./tasks/RestoreSchemaSnapshot.md)

    Load a schema by restoring a snapshot.

### [UnloadSchema](./tasks/UnloadSchema.md)

    Unload a schema.

### [LoadedSchemas](./tasks/LoadedSchemas.md)

    Retrieve some information about a loaded schema.

### [SchemaInfo](./tasks/SchemaInfo.md)

    Retrieve some information about a deployed schema.

### [DeleteSchemaBackup](./tasks/DeleteSchemaBackup.md)

    Delete a backup.

## MDX Execution

### [ExecuteMdx](./tasks/ExecuteMdx.md)

    Execute an MDX statement and assert its result using another equivalent MDX statement.

### [MDXes](./tasks/MDXes.md)

    Execute a list of MDX statements and assert their results using a list of previously generated results.

### [GenerateMDXes](./tasks/GenerateMDXes.md)

    Generates the expected results of the task `MDXes`.

## Dashboards

### [OpenReport](./tasks/OpenReport.md)

    Open a report using a local `Chrome` instance.

### [PrintReport](./tasks/PrintReport.md)

    Request the icCube server to print a report (requires an icCube server v8.4.10).

_
