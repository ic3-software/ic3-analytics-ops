package ic3.analyticsops.test;

import com.google.gson.*;
import ic3.analyticsops.test.assertion.AOAssertionDeserializer;
import ic3.analyticsops.test.task.mdx.AOExecuteMdxTask;
import ic3.analyticsops.test.task.mdx.AOGenerateMDXesTask;
import ic3.analyticsops.test.task.mdx.AOMDXesTask;
import ic3.analyticsops.test.task.reporting.AOOpenReportTask;
import ic3.analyticsops.test.task.reporting.AOPrintReportTask;
import ic3.analyticsops.test.task.schema.*;
import ic3.analyticsops.test.task.server.AOClearResultCacheTask;
import ic3.analyticsops.test.task.server.AOServerStatusTask;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AOTaskDeserializer implements JsonDeserializer<AOTask>
{
    private static final Map<AOTaskID, Class<? extends AOTask>> classes = new HashMap<>();

    static
    {
        classes.put(AOTaskID.ClearResultCache, AOClearResultCacheTask.class);
        classes.put(AOTaskID.DeleteSchemaBackup, AODeleteSchemaBackupTask.class);
        classes.put(AOTaskID.GenerateMDXes, AOGenerateMDXesTask.class);
        classes.put(AOTaskID.LoadedSchemas, AOLoadedSchemasTask.class);
        classes.put(AOTaskID.LoadSchema, AOLoadSchemaTask.class);
        classes.put(AOTaskID.MDX, AOExecuteMdxTask.class);
        classes.put(AOTaskID.MDXes, AOMDXesTask.class);
        classes.put(AOTaskID.OpenReport, AOOpenReportTask.class);
        classes.put(AOTaskID.PrintReport, AOPrintReportTask.class);
        classes.put(AOTaskID.RestoreSchemaBackup, AORestoreSchemaBackupTask.class);
        classes.put(AOTaskID.RestoreSchemaSnapshot, AORestoreSchemaSnapshotTask.class);
        classes.put(AOTaskID.SchemaInfo, AOSchemaInfoTask.class);
        classes.put(AOTaskID.ServerStatus, AOServerStatusTask.class);
        classes.put(AOTaskID.UnloadSchema, AOUnloadSchemaTask.class);
    }

    @Override
    public AOTask<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException
    {
        final JsonObject obj = jsonElement.getAsJsonObject();
        final AOTaskID id = AOTaskID.valueOf(obj.get("action").getAsString());
        final Class<? extends AOTask> clazz = classes.get(id);

        final Gson gson = new GsonBuilder()
                .setLenient()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(AOAssertion.class, new AOAssertionDeserializer(id))
                .create();

        final AOTask<?> task = gson.fromJson(jsonElement, clazz);

        return task;
    }
}
