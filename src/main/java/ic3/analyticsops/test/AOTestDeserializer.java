package ic3.analyticsops.test;

import com.google.gson.*;
import ic3.analyticsops.gson.AODurationTypeAdapter;

import java.io.File;
import java.lang.reflect.Type;
import java.time.Duration;

public class AOTestDeserializer implements JsonDeserializer<AOTest>
{
    private final File json;

    public AOTestDeserializer(File json)
    {
        this.json = json;
    }

    @Override
    public AOTest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException
    {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(AOTest.class, new AOTestInstanceCreator(json))
                .registerTypeAdapter(AOTask.class, new AOTaskDeserializer())
                .registerTypeAdapter(Duration.class, new AODurationTypeAdapter())
                .create();

        final AOTest test = gson.fromJson(jsonElement, AOTest.class);

        test.onFromJson();

        return test;
    }
}
