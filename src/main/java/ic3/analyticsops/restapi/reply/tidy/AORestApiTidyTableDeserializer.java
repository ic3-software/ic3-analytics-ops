package ic3.analyticsops.restapi.reply.tidy;

import com.google.gson.*;
import ic3.analyticsops.restapi.reply.tidy.drillthrough.AORestApiDrillthroughTidyTable;
import ic3.analyticsops.restapi.reply.tidy.flat.AORestApiFlatTidyTable;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTable;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableColumn;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableColumnDeserializer;

import java.lang.reflect.Type;

public class AORestApiTidyTableDeserializer implements JsonDeserializer<AORestApiTidyTable<?>>
{
    @Override
    public AORestApiTidyTable<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        final JsonObject obj = jsonElement.getAsJsonObject();

        final String classID = obj.get("classID").getAsString();

        final Class<?> clazz;

        if (AORestApiTidyTableClassID.TIDY_TABLE.name().equals(classID))
        {
            clazz = AORestApiFlatTidyTable.class;
        }
        else if (AORestApiTidyTableClassID.MDX_TABLE.name().equals(classID))
        {
            clazz = AORestApiMdxTidyTable.class;
        }
        else if (AORestApiTidyTableClassID.MDX_DRILLTHROUGH_TABLE.name().equals(classID))
        {
            clazz = AORestApiDrillthroughTidyTable.class;
        }
        else
        {
            throw new RuntimeException("unexpected classID [" + classID + "]");
        }

        final Gson gson = new GsonBuilder()
                // re-entrant w/ steps
                .registerTypeAdapter(AORestApiTidyTable.class, new AORestApiTidyTableDeserializer())
                .registerTypeAdapter(AORestApiMdxTidyTableColumn.class, new AORestApiMdxTidyTableColumnDeserializer())
                .create();

        final Object t = gson.fromJson(jsonElement, clazz);
        final AORestApiTidyTable<?> table = (AORestApiTidyTable<?>) t;

        table.bindColumns();

        return table;
    }
}
