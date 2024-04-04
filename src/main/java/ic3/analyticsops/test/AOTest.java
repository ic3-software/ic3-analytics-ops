package ic3.analyticsops.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.client.AORestApiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AOTest extends AOSerializable
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected transient final File json;

    private final String name;

    /**
     * Possibly defined in each actor.
     */
    @Nullable
    private final String restApiURL;

    /**
     * Possibly defined in each actor.
     */
    @Nullable
    private final AOAuthenticator authenticator;

    private final List<AOActor> actors;

    protected AOTest(File json)
    {
        this.json = json;

        // JSON deserialization

        this.name = null;
        this.restApiURL = null;
        this.authenticator = null;
        this.actors = null;
    }

    public static AOTest create(File json)
            throws IOException
    {
        final List<String> lines = processSystemProperties(Files.readAllLines(json.toPath(), StandardCharsets.UTF_8));

        final String content = String.join("\n", lines);

        final Gson gson = new GsonBuilder()
                .setLenient()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(AOTest.class, new AOTestDeserializer(json))
                .create();

        final AOTest tst = gson.fromJson(content, AOTest.class);

        return tst;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson()
    {
        if (actors != null)
        {
            if (!actors.isEmpty() && actors.getLast() == null)
            {
                actors.removeLast() /* trailing comma in JSON5 [] */;
            }

            for (int aa = 0; aa < actors.size(); aa++)
            {
                final AOActor actor = actors.get(aa);
                actor.onFromJson(this, aa);
            }
        }
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField("name", name);

        validateActors();
    }

    public void validateActors()
            throws AOTestValidationException
    {
        validateNonEmptyField("actors", actors);

        int active = 0;

        for (AOActor actor : actors)
        {
            if (actor.isActive())
            {
                active++;
            }
        }

        // Guess it makes no sense to run a test wo/ any active actor : let's throw an error right now.

        if (active == 0)
        {
            throw new AOTestValidationException("the JSON field 'actors' has no active actor");
        }

        for (AOActor actor : actors)
        {
            if (actor.isActive())
            {
                actor.validate();
            }
        }
    }

    @Nullable
    public String getRestApiURL()
    {
        return restApiURL;
    }

    @Nullable
    public AOAuthenticator getAuthenticator()
    {
        return authenticator;
    }

    /**
     * Defaulted to the parent folder of the JSON5 test configuration.
     */
    public File getMDXesDataFolder(String data)
    {
        return new File(json.getParentFile(), data);
    }

    public void run(AOTestContext context)
            throws AOException
    {
        for (AOActor actor : actors /* validated by now */)
        {
            if (!actor.isActive())
            {
                continue;
            }

            final String restApiURL = actor.getRestApiURL(this.restApiURL);
            final AOAuthenticator authenticator = actor.getAuthenticator(this.authenticator);

            final AORestApiClient client = new AORestApiClient(restApiURL, authenticator);
            final AOActorContext aContext = new AOActorContext(context, client);

            actor.run(aContext);
        }
    }

    /**
     * Replace Java system properties (leave unchanged if the property is not defined).
     * For example, assuming the following properties:
     * <pre>
     *     -Danalytics.ops.user=admin
     *     -Danalytics.ops.password=admin
     * </pre>
     * The following input :
     * <pre>
     * authenticator: {
     *   user: "${analytics.ops.user}",
     *   password: "${analytics.ops.password}"
     * }
     * </pre>
     * is transformed into :
     * <pre>
     * authenticator: {
     *   user: "admin",
     *   password: "admin"
     * }
     */
    private static List<String> processSystemProperties(List<String> lines)
    {
        final Pattern pattern = Pattern.compile("\\$\\{(?<prop>[A-Za-z0-9-_.]+)}");

        final List<String> processed = new ArrayList<>();

        for (String line : lines)
        {
            processed.add(replaceTokens(line, pattern, match ->
            {
                final String prop = match.group("prop");
                final String value = System.getProperty(prop);
                return value != null ? value : match.group();
            }));
        }

        return processed;
    }

    private static String replaceTokens(String original, Pattern tokenPattern, Function<Matcher, String> converter)
    {
        int lastIndex = 0;

        StringBuilder output = new StringBuilder();

        Matcher matcher = tokenPattern.matcher(original);

        while (matcher.find())
        {
            output.append(original, lastIndex, matcher.start()).append(converter.apply(matcher));

            lastIndex = matcher.end();
        }

        if (lastIndex < original.length())
        {
            output.append(original, lastIndex, original.length());
        }

        return output.toString();
    }

}
