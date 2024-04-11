package ic3.analyticsops.test;

import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.List;

public class AOAuthenticator extends AOSerializable
{
    @Nullable
    public final String user;

    @Nullable
    public final String password;

    @Nullable
    public final List<AOHeader> headers;

    protected AOAuthenticator()
    {
        // JSON deserialization

        this.user = null;
        this.password = null;
        this.headers = null;
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate(String prefix)
            throws AOTestValidationException
    {
        if (headers != null)
        {
            validateEmptyField(prefix + "user", user);
            validateEmptyField(prefix + "password", password);

            validateNonEmptyField(prefix + "headers", headers);

            for (int hh = 0; hh < headers.size(); hh++)
            {
                final AOHeader header = headers.get(hh);

                header.validate(prefix + "header[" + hh + "].");
            }
        }
        else
        {
            validateNonEmptyField(prefix + "user", user);
            validateNonEmptyField(prefix + "password", password);
        }
    }

    public HttpRequest.Builder addHeaders(HttpRequest.Builder builder)
    {
        if (headers != null)
        {
            for (AOHeader header : headers)
            {
                builder = builder.header(header.name, header.value);
            }
        }
        else
        {
            final String credentials = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());

            builder = builder.header("X-AUTHORIZATION", credentials);
        }

        return builder;
    }
}
