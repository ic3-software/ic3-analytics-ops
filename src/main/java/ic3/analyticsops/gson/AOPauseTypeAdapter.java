package ic3.analyticsops.gson;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ic3.analyticsops.common.AOFixedPause;
import ic3.analyticsops.common.AOPause;
import ic3.analyticsops.common.AORandomPause;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public class AOPauseTypeAdapter extends TypeAdapter<AOPause>
{
    @Override
    public void write(JsonWriter writer, AOPause pause)
            throws IOException
    {
        if (pause == null)
        {
            writer.nullValue();
            return;
        }
        writer.value(pause.toString());
    }

    @Override
    public AOPause read(JsonReader reader)
            throws IOException
    {
        if (reader.peek() == JsonToken.NULL)
        {
            reader.nextNull();
            return null;
        }

        final String definition = reader.nextString();
        final String[] parts = definition.split(":");

        if (parts.length == 1)
        {
            try
            {
                final Duration duration = Duration.parse(parts[0]);

                return new AOFixedPause(duration);
            }
            catch (DateTimeParseException ex)
            {
                throw new JsonSyntaxException("invalid pause [" + definition + "] : " + ex.getMessage());
            }
        }
        else if (parts.length == 2)
        {
            try
            {
                final Duration first = Duration.parse(parts[0]);
                final Duration second = Duration.parse(parts[1]);

                return new AORandomPause(first, second);
            }
            catch (DateTimeParseException ex)
            {
                throw new JsonSyntaxException("invalid pause [" + definition + "] : " + ex.getMessage());
            }
        }
        else
        {
            throw new JsonSyntaxException("invalid pause [" + definition + "]");
        }
    }
}
