package ic3.analyticsops.test.assertion;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AOAssertion
{
    public static void assertTrue(String message, boolean value)
    {
        if (!value)
        {
            fail(format(message, "true", value));
        }
    }

    public static void assertNotNull(String message, @Nullable Object value)
    {
        if (value == null)
        {
            fail(format(message, "not-null", value));
        }
    }

    public static void assertNull(String message, @Nullable Object value)
    {
        if (value != null)
        {
            fail(format(message, "null", value));
        }
    }

    public static void assertEquals(String message, @Nullable Object expected, @Nullable Object actual)
    {
        if (expected != null || actual != null)
        {
            if (expected == null || !equals(expected, actual))
            {
                failNotEquals(message, expected, actual);
            }
        }
    }

    private static boolean equals(Object expected, @Nullable Object actual)
    {
        return Objects.equals(expected, actual);
    }

    private static String format(String message, @Nullable Object expected, @Nullable Object actual)
    {
        String formatted = "";

        if (message != null && !message.isEmpty())
        {
            formatted = message + " ";
        }

        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }

    private static void failNotEquals(String message, @Nullable Object expected, @Nullable Object actual)
    {
        fail(format(message, expected, actual));
    }

    public static void fail(@Nullable String message)
    {
        if (message == null)
        {
            throw new AssertionError();
        }

        throw new AssertionError(message);
    }

}
