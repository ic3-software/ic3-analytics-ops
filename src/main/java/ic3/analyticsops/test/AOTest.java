package ic3.analyticsops.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.task.reporting.AOChromeException;
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

public class AOTest
{
    private transient File json;

    private String name;

    /**
     * Possibly defined in each actor.
     */
    @Nullable
    private String restApiURL;

    /**
     * Possibly defined in each actor.
     */
    @Nullable
    private AOAuthenticator authenticator;

    private List<AOActor> actors;

    public static AOTest create(File json)
            throws IOException
    {
        final List<String> lines = processSystemProperties(
                Files.readAllLines(json.toPath(), StandardCharsets.UTF_8)
        );

        final String content = String.join("\n", lines);

        final Gson gson = new GsonBuilder()
                .setLenient()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(AOTask.class, new AOTaskDeserializer())
                .create();

        final AOTest tst = gson.fromJson(content, AOTest.class);

        tst.json = json;

        return tst;
    }

    public File getMDXesDataFolder(String data)
    {
        return new File(json.getParentFile(), data);
    }

    public void run(AOTestContext context)
            throws AORestApiException,
                   AOChromeException
    {
        if (actors != null)
        {
            for (AOActor actor : actors)
            {
                if (!actor.isActive())
                {
                    continue;
                }

                final String restApiURL = actor.getRestApiURL(this.restApiURL);

                if (restApiURL == null)
                {
                    throw new AORestApiException("missing restApiURL from test/actor");
                }

                final AOAuthenticator authenticator = actor.getAuthenticator(this.authenticator);

                if (authenticator == null)
                {
                    throw new AORestApiException("missing authenticator from test/actor");
                }

                final AORestApiClient client = new AORestApiClient(restApiURL, authenticator);
                final AOActorContext aContext = new AOActorContext(context, client);

                actor.run(aContext);
            }
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
