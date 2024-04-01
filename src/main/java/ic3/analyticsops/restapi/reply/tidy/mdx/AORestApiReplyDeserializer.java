package ic3.analyticsops.restapi.reply.tidy.mdx;

import com.google.gson.*;
import ic3.analyticsops.restapi.error.AORestApiError;
import ic3.analyticsops.restapi.reply.AORestApiErrorReply;
import ic3.analyticsops.restapi.reply.AORestApiReply;
import ic3.analyticsops.restapi.reply.AORestApiSuccessReply;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableDeserializer;

import java.lang.reflect.Type;

public class AORestApiReplyDeserializer<REPLY> implements JsonDeserializer<AORestApiReply<REPLY>>
{
    private final Class<REPLY> payloadClass;

    public AORestApiReplyDeserializer(Class<REPLY> payloadClass)
    {
        this.payloadClass = payloadClass;
    }

    @Override
    public AORestApiReply<REPLY> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        final JsonObject obj = json.getAsJsonObject();

        final JsonPrimitive status = obj.getAsJsonPrimitive("status");
        final JsonObject payload = obj.getAsJsonObject("payload");

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AORestApiTidyTable.class, new AORestApiTidyTableDeserializer())
                .create();

        if (!"OK".equalsIgnoreCase(status.getAsString()))
        {
            final AORestApiError error = gson.fromJson(payload, AORestApiError.class);
            return new AORestApiErrorReply<>(error);
        }

        final REPLY reply = gson.fromJson(payload, payloadClass);
        return new AORestApiSuccessReply<>(reply);
    }

}
