package ic3.analyticsops.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ic3.analyticsops.test.load.AOLoadTestConfiguration;
import ic3.analyticsops.test.schedule.AOActorSchedule;
import ic3.analyticsops.test.schedule.AOTestSchedule;
import ic3.analyticsops.test.task.reporting.AOChromeConfiguration;
import ic3.analyticsops.utils.AODurationUtils;
import ic3.analyticsops.utils.AOLog4jUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AOTest extends AOSerializable
{
    protected transient final File json;

    private final String name;

    /**
     * Possibly overridden in each actor.
     */
    @Nullable
    private final String restApiURL;

    /**
     * Possibly overridden in each actor.
     */
    @Nullable
    private final AOAuthenticator authenticator;

    /**
     * REST API request timeout.
     * Possibly overridden in each actor.
     */
    @Nullable
    private final Duration timeout;

    @Nullable
    private final AOChromeConfiguration chrome;

    @Nullable
    private final Duration duration;

    private final List<AOActor> actors;

    /**
     * An optional configuration to make this test running as a load-test : stress, ...
     */
    @Nullable
    private final AOLoadTestConfiguration load;

    protected AOTest(File json)
    {
        this.json = json;

        // JSON deserialization

        this.name = null;
        this.restApiURL = null;
        this.authenticator = null;
        this.timeout = null;
        this.chrome = null;
        this.duration = null;
        this.actors = null;
        this.load = null;
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

        if (load != null)
        {
            load.onFromJson(this);
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
        validateLoadConfiguration();
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
            throw new AOTestValidationException("the JSON field 'actors' is referencing non-active actors only");
        }

        for (AOActor actor : actors)
        {
            if (actor.isActive())
            {
                actor.validate();
            }
        }

        // Ensure names are unique : load-test configuration is referencing actors by their name.

        final Set<String> names = new HashSet<>();

        for (AOActor actor : actors)
        {
            if (names.contains(actor.getName()))
            {
                throw new AOTestValidationException("the JSON field 'actors' contains several actors with the same name : " + actor.getName());
            }
            names.add(actor.getName());
        }
    }

    public void validateLoadConfiguration()
            throws AOTestValidationException
    {
        if (load != null)
        {
            load.validate();

            if (duration != null)
            {
                // The actual duration of the test is defined by the duration of all the stages.
                throw new AOTestValidationException("the JSON field 'duration' cannot be used with load testing");
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

    @Nullable
    public Duration getTimeout()
    {
        return timeout;
    }

    @Nullable
    public AOChromeConfiguration getChromeConfiguration()
    {
        return chrome;
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

    @Nullable
    public AOLoadTestConfiguration getLoad()
    {
        return load;
    }

    @Nullable
    public AOActor lookupActor(String name)
    {
        if (actors != null)
        {
            for (AOActor actor : actors)
            {
                if (name.equals(actor.getName()))
                {
                    return actor;
                }
            }
        }

        return null;
    }

    @Nullable
    public AOActor lookupActiveActor(String name)
    {
        final AOActor actor = lookupActor(name);

        if (actor != null && actor.isActive())
        {
            return actor;
        }

        return null;
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
        final AOTestSchedule schedule = context.getSchedule();

        if (!schedule.isLoadTesting())
        {
            final Duration duration = schedule.getDuration();

            if (duration != null)
            {
                AOLog4jUtils.TEST.info(
                        "[test] the test will run for {} [duration : {}]",
                        AODurationUtils.formatMillis(duration.toMillis()),
                        duration
                );
            }
            else
            {
                AOLog4jUtils.TEST.info("[test] the test will execute once every actor.");
            }
        }
        else
        {
            AOLog4jUtils.TEST.info(
                    "[test] the load test will run for {}",
                    AODurationUtils.formatMillis(schedule.getLoadTestingMaxDurationMS())
            );
        }

        final List<AOActorSchedule> schedules = schedule.getActors();

        final List<AOActorContext> actorsContexts = schedules.stream()
                .map(s -> new AOActorContext(context, s))
                .toList();

        final long nowMS = System.currentTimeMillis();

        for (AOActorContext actorContext : actorsContexts)
        {
            context.attachActorContext(actorContext);
            actorContext.run(nowMS);
        }

        AOLog4jUtils.TEST.info("[test] waiting for {} actors", actorsContexts.size());

        context.waitForCompletion();

        AOLog4jUtils.TEST.info("[test] completed");
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
