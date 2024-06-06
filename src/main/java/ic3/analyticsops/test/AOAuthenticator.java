package ic3.analyticsops.test;

import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.List;

public class AOAuthenticator extends AOSerializable
{
    @Nullable
    private final String user;

    @Nullable
    private final String password;

    @Nullable
    private final List<AOHeader> headers;

    protected AOAuthenticator()
    {
        // JSON deserialization

        this.user = null;
        this.password = null;
        this.headers = null;
    }

    public AOAuthenticator(@Nullable String user, @Nullable String password)
    {
        this.user = user;
        this.password = password;
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
            validateNullField(prefix + "user", user);
            validateNullField(prefix + "password", password);

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

    /**
     * Assuming this has been validated.
     */
    public boolean isFormAuth()
    {
        return headers == null;
    }

    /**
     * Assuming this has been validated.
     */
    public boolean isHeadersAuth()
    {
        return headers != null;
    }

    /**
     * Assuming this has been validated and isFormAuth().
     */
    public String getUser()
    {
        if (user == null)
        {
            throw new RuntimeException("internal error : unexpected missing user");
        }
        return user;
    }

    /**
     * Assuming this has been validated and isFormAuth().
     */
    public String getPassword()
    {
        if (password == null)
        {
            throw new RuntimeException("internal error : unexpected missing password");
        }
        return password;
    }

    /**
     * Assuming this has been validated and isHeadersAuth().
     */
    public List<AOHeader> getHeaders()
    {
        if (headers == null)
        {
            throw new RuntimeException("internal error : unexpected missing headers");
        }
        return headers;
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
