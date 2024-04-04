package ic3.analyticsops.test;

import ic3.analyticsops.utils.AOStringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AOSerializable
{
    public void validateNonEmptyField(String path, @Nullable Object value)
            throws AOTestValidationException
    {
        if (value == null)
        {
            throw new AOTestValidationException("the JSON field '" + path + "' cannot be null");
        }

        if (value instanceof String valueS && AOStringUtils.isEmpty(valueS))
        {
            throw new AOTestValidationException("the JSON field '" + path + "' cannot be empty");
        }
    }

    public <VALUE> void validateNonEmptyField(String path, @Nullable List<VALUE> values)
            throws AOTestValidationException
    {
        if (values == null || values.isEmpty())
        {
            throw new AOTestValidationException("the JSON field '" + path + "' cannot be null/empty");
        }
    }

    /**
     * At least one value is not null/empty.
     */
    public void validateNonEmptyFields(String path, Object... values)
            throws AOTestValidationException
    {
        if (values != null && values.length > 0)
        {
            for (Object value : values)
            {
                if (value instanceof String valueS && AOStringUtils.isNotEmpty(valueS))
                {
                    return;
                }
                if (value != null)
                {
                    return;
                }
            }
        }

        throw new AOTestValidationException("the JSON fields '" + path + "' are all null/empty");
    }

    public <VALUE> void validateEmptyField(String path, @Nullable List<VALUE> values)
            throws AOTestValidationException
    {
        if (values != null && !values.isEmpty())
        {
            throw new AOTestValidationException("the JSON field '" + path + "' is not null/empty");
        }
    }

}
