package ic3.analyticsops.restapi.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ic3.analyticsops.restapi.error.AORestApiErrorException;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.error.AORestApiHttpException;
import ic3.analyticsops.restapi.reply.AORestApiReply;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiReplyDeserializer;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.AOAuthenticator;
import ic3.analyticsops.utils.AOLog4jUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.zip.GZIPInputStream;

public class AORestApiClient
{
    private final String restApiURL;

    private final AOAuthenticator authenticator;

    public AORestApiClient(String restApiURL, AOAuthenticator authenticator)
    {
        this.restApiURL = restApiURL;
        this.authenticator = authenticator;
    }

    public String getRestApiURL()
    {
        return restApiURL;
    }

    public AOAuthenticator getAuthenticator()
    {
        return authenticator;
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request)
            throws AORestApiException
    {
        return sendRequest(request, null);
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request, @Nullable AORestApiClientOptions options)
            throws AORestApiException
    {
        try
        {
            String command = request.getCommand();
            final String params = request.getParamsAsJson();

            final URI uri = new URI(restApiURL + command);

            final HttpClient.Builder httpClientB = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1);

            try (final HttpClient httpClient = httpClientB.build())
            {
                final HttpRequest.Builder httpRequestBuilder = authenticator.addHeaders(
                        HttpRequest.newBuilder(uri)
                                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                                .header("Accept-Encoding", "gzip")
                                .header("Content-Type", "application/json;charset=UTF-8")
                                .POST(HttpRequest.BodyPublishers.ofString(params))
                );

                final HttpRequest httpRequest = httpRequestBuilder.build();

                final HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

                final int statusCode = httpResponse.statusCode();

                if (statusCode >= 300)
                {
                    throw new AORestApiHttpException(statusCode);
                }

                try (final InputStream is = responseInputStream(httpResponse, options))
                {
                    return parseReply(request.getReplyClass(), is, options != null && options.withJson);
                }
            }
        }
        catch (IOException | URISyntaxException | InterruptedException ex)
        {
            throw new AORestApiException("send request error", ex);
        }
    }

    public static <REPLY> REPLY parseReply(Class<REPLY> clazz, InputStream is, boolean withJson)
            throws AORestApiErrorException,
                   IOException
    {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AORestApiReply.class, new AORestApiReplyDeserializer<>(clazz))
                .create();

        try (final Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8))
        {
            // ---------------------------------------------------------------------------------------------------------
            // With or without JSON requested, let's first read the whole JSON in memory in case of error :
            //      e.g., reply = "the user is not authorized ..." instead of a valid JSON
            // I guess this is not an issue for now.
            // ---------------------------------------------------------------------------------------------------------

            final String json = IOUtils.toString(reader);
            final AORestApiReply<REPLY> reply;

            try
            {
                reply = gson.<AORestApiReply<REPLY>>fromJson(json, AORestApiReply.class);
            }
            catch (RuntimeException ex)
            {
                AOLog4jUtils.SHELL.error("[reply] unexpected REST API reply : {}", json);
                throw ex;
            }

            if (reply.isError())
            {
                throw new AORestApiErrorException(reply.getError());
            }

            final REPLY payload = reply.getPayload();

            if (withJson && payload instanceof AORestApiMdxScriptResult res)
            {
                res.json = json;
            }

            return payload;
        }
    }

    private InputStream responseInputStream(HttpResponse<InputStream> httpResponse, @Nullable AORestApiClientOptions options)
            throws IOException
    {
        final HttpHeaders headers = httpResponse.headers();

        final String encoding = headers.firstValue("Content-Encoding").orElse(null);

        final InputStream is = "gzip".equalsIgnoreCase(encoding) ? new GZIPInputStream(httpResponse.body()) : httpResponse.body();

        if (options != null && options.dumpJson)
        {
            // Ok for now to get the whole result in RAM - later better to use a temp. file...
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            is.transferTo(out);

            final byte[] bytes = out.toByteArray();

            final String json = new String(bytes, StandardCharsets.UTF_8);

            AOLog4jUtils.DUMP_JSON.warn("[dump-json] [{}] [{}]", bytes.length, json);

            return new ByteArrayInputStream(bytes);
        }

        return is;
    }

}
