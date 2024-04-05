package ic3.analyticsops.gson;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public class AODurationTypeAdapter extends TypeAdapter<Duration>
{
    @Override
    public void write(JsonWriter writer, Duration duration)
            throws IOException
    {
        if (duration == null)
        {
            writer.nullValue();
            return;
        }
        writer.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader reader)
            throws IOException
    {
        if (reader.peek() == JsonToken.NULL)
        {
            reader.nextNull();
            return null;
        }
        final String duration = reader.nextString();
        try
        {
            return Duration.parse(duration);
        }
        catch (DateTimeParseException ex)
        {
            throw new JsonSyntaxException("invalid duration [" + duration + "] : " + ex.getMessage());
        }
    }
}
