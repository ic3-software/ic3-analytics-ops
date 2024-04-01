package ic3.analyticsops.restapi.reply.tidy.mdx;

import com.google.gson.*;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableClassID;

import java.lang.reflect.Type;

public class AORestApiMdxTidyTableColumnDeserializer implements JsonDeserializer<AORestApiMdxTidyTableColumn>
{
    @Override
    public AORestApiMdxTidyTableColumn deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        final JsonObject obj = jsonElement.getAsJsonObject();

        final String classID = obj.get("classID").getAsString();

        final Class<?> clazz;

        if (AORestApiTidyTableClassID.MDX_MEMBERS.name().equals(classID))
        {
            clazz = AORestApiMdxTidyTableMemberColumn.class;
        }
        else if (AORestApiTidyTableClassID.MDX_CELLS.name().equals(classID))
        {
            clazz = AORestApiMdxTidyTableCellColumn.class;
        }
        else
        {
            throw new RuntimeException("unexpected classID [" + classID + "]");
        }

        final Gson gson = new GsonBuilder().create();

        final Object c = gson.fromJson(jsonElement, clazz);

        return (AORestApiMdxTidyTableColumn) c;
    }
}
