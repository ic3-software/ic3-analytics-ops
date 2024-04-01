package ic3.analyticsops.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class AOStringUtils
{
    private AOStringUtils()
    {
    }

    @Contract("null -> false")
    public static boolean isNotEmpty(@Nullable String string)
    {
        return !isEmpty(string);
    }

    @Contract("null -> true")
    public static boolean isEmpty(@Nullable String string)
    {
        if (isStrictlyEmptyOrNull(string))
        {
            return true;
        }
        for (int i = 0; i < string.length(); i++)
        {
            final char c = string.charAt(i);
            if (!Character.isWhitespace(c) && c != '\t')
            {
                return false;
            }
        }
        return true;
    }

    @Contract("null -> true")
    public static boolean isStrictlyEmptyOrNull(@Nullable String string)
    {
        return string == null || string.isEmpty();
    }

}
