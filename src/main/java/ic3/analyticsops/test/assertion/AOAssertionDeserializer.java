package ic3.analyticsops.test.assertion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTaskID;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AOAssertionDeserializer implements JsonDeserializer<AOAssertion>
{
    private static final Map<AOTaskID, Class<? extends AOAssertion>> classes = new HashMap<>();

    static
    {
        classes.put(AOTaskID.MDX, AOExecuteMdxAssertion.class);
        classes.put(AOTaskID.SchemaInfo, AOSchemaInfoAssertion.class);
        classes.put(AOTaskID.ServerStatus, AOServerStatusAssertion.class);
    }

    private final AOTaskID id;

    public AOAssertionDeserializer(AOTaskID id)
    {
        this.id = id;
    }

    @Override
    public AOAssertion deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException
    {
        final Class<? extends AOAssertion> clazz = classes.get(id);

        AOAssertion assertion = null;

        if (clazz != null)
        {
            assertion = context.deserialize(jsonElement, clazz);
        }

        return assertion;
    }
}
