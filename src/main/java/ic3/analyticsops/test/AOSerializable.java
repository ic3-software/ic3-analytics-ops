package ic3.analyticsops.test;

import ic3.analyticsops.utils.AOStringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public abstract class AOSerializable
{
    @Contract("_,!null -> fail")
    public void validateNullField(String path, @Nullable Object value)
            throws AOTestValidationException
    {
        if (value != null)
        {
            throw new AOTestValidationException("the JSON field '" + path + "' must be null");
        }
    }

    @Contract("_,null -> fail")
    public void validateNonEmptyField(String path, @Nullable Object value)
            throws AOTestValidationException
    {
        if (isNullOrEmpty(value))
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
        if (values != null)
        {
            for (Object value : values)
            {
                if (!isNullOrEmpty(value))
                {
                    return;
                }
            }
        }

        throw new AOTestValidationException("the JSON fields '" + path + "' are all null/empty");
    }

    @Contract("null -> true")
    private boolean isNullOrEmpty(@Nullable Object value)
    {
        return switch (value)
        {
            case String valueS when AOStringUtils.isEmpty(valueS) -> true;

            case Collection<?> valueC when valueC.isEmpty() -> true;

            case Map<?, ?> valueM when valueM.isEmpty() -> true;

            case null -> true;

            default -> false;
        };
    }
}
