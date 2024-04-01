package ic3.analyticsops.restapi.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AORestApiRequest<REPLY>
{
    static final String URL_CONSOLE = "/console";

    static final String URL_ADMIN = URL_CONSOLE + "/admin";

    static final String URL_MDX = URL_CONSOLE + "/mdx";

    private final String command;
    private final Class<REPLY> reply;

    private final List<Pair> params = new ArrayList<>();

    public AORestApiRequest(String command, Class<REPLY> reply)
    {
        this.command = command;
        this.reply = reply;
    }

    public String getCommand()
    {
        return this.command;
    }

    public String getParamsAsJson()
    {
        final Map<String, Object> map = new HashMap<>();

        for (Pair pair : params)
        {
            final Object value = map.get(pair.name());

            if (value == null)
            {
                map.put(pair.name(), pair.value());
            }
            else if (value instanceof List)
            {
                ((List) value).add(pair.value());
            }
            else
            {
                final List<String> values = new ArrayList<>();

                values.add((String) value);
                values.add(pair.value());

                map.put(pair.name(), values);
            }
        }

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        return gson.toJson(map);
    }

    public Class<REPLY> getReplyClass()
    {
        return reply;
    }

    protected AORestApiRequest<REPLY> addParam(String name, String value)
    {
        params.add(new Pair(name, value));

        return this;
    }

    record Pair(String name, String value)
    {
    }
}
