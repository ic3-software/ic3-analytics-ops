package ic3.analyticsops.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.utils.AOLog4jUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AOTest extends AOSerializable
{
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

    @Nullable
    private final Duration duration;

    private final List<AOActor> actors;

    protected AOTest(File json)
    {
        this.json = json;

        // JSON deserialization

        this.name = null;
        this.restApiURL = null;
        this.authenticator = null;
        this.duration = null;
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

        if (duration != null)
        {
            if (duration.isNegative() || duration.isZero())
            {
                throw new AOTestValidationException("the JSON field 'duration' cannot be negative or zero [" + duration + "]");
            }
        }

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

    @Nullable
    public Duration getDuration()
    {
        return duration;
    }

    public List<AOActor> activeActors()
    {
        return actors/* validated by now */.stream().filter(AOActor::isActive).toList();
    }

    /**
     * Blocking call.
     */
    public void run(AOTestContext context)
            throws InterruptedException
    {
        final Duration duration = getDuration();

        if (duration != null)
        {
            AOLog4jUtils.TEST.info("[test] duration : {}", duration);
        }
        else
        {
            AOLog4jUtils.TEST.info("[test] duration : once");
        }

        final List<AOActor> activeActors = activeActors();

        for (AOActor actor : activeActors)
        {
            final String restApiURL = actor.getRestApiURL(this.restApiURL);
            final AOAuthenticator authenticator = actor.getAuthenticator(this.authenticator);

            final AORestApiClient client = new AORestApiClient(restApiURL, authenticator);
            final AOActorContext aContext = new AOActorContext(context, client, actor);

            actor.run(aContext) /* in its own thread of control */;
        }

        AOLog4jUtils.TEST.info("[test] waiting for {} actors", activeActors.size());

        context.waitForCompletion();

        AOLog4jUtils.TEST.info("[test] waiting for {} actors done", activeActors.size());
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
