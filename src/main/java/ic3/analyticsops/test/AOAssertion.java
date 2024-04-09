package ic3.analyticsops.test;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public abstract class AOAssertion extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOTask<?> jsonParent;

    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient int jsonAssertionNb;

    protected AOAssertion()
    {
        // JSON deserialization
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOTask<?> jsonParent, int jsonAssertionNb)
    {
        this.jsonParent = jsonParent;
        this.jsonAssertionNb = jsonAssertionNb;
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate()
            throws AOTestValidationException
    {
    }

    public String validateFieldPathPrefix()
    {
        return "actors[" + jsonParent.jsonParentActor.jsonActorNb + "].tasks[" + jsonParent.jsonTaskNb + "].assertions[" + jsonAssertionNb + "].";
    }

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
            if (expected == null || !equals(expected, actual, 0, null))
            {
                failNotEquals(message, expected, actual);
            }
        }
    }

    public static boolean assertEquals(String message, @Nullable Object expected, @Nullable Object actual, double delta)
    {
        final boolean[] deltaApplied = delta > 0 ? new boolean[1] : null;

        if (expected != null || actual != null)
        {
            if (expected == null || !equals(expected, actual, delta, deltaApplied))
            {
                failNotEquals(message, expected, actual);
            }
        }

        return deltaApplied != null && deltaApplied[0];
    }

    public static boolean equals(Object expected, @Nullable Object actual, double delta, @Nullable boolean[] deltaApplied)
    {
        if (expected instanceof Double && actual instanceof Double)
        {
            return equals((double) expected, (double) actual, delta, deltaApplied);
        }
        else if (expected instanceof Float && actual instanceof Float)
        {
            return equals((float) expected, (float) actual, delta, deltaApplied);
        }
        else if (expected instanceof Map && actual instanceof Map)
        {
            return equals((Map<?, ?>) expected, (Map<?, ?>) actual, delta, deltaApplied);
        }
        else if (expected instanceof ArrayList && actual instanceof ArrayList)
        {
            return equals((ArrayList<?>) expected, (ArrayList<?>) actual, delta, deltaApplied);
        }
        return Objects.equals(expected, actual);
    }

    public static boolean equals(double expected, double actual, double delta, @Nullable boolean[] deltaApplied)
    {
        if (Double.compare(expected, actual) != 0)
        {
            if (Math.abs(expected - actual) <= delta)
            {
                if (delta > 0)
                {
                    deltaApplied[0] = true;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(float expected, float actual, double delta, @Nullable boolean[] deltaApplied)
    {
        if (Float.compare(expected, actual) != 0)
        {
            if (Math.abs(expected - actual) <= delta)
            {
                if (delta > 0)
                {
                    deltaApplied[0] = true;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Map<?, ?> expected, Map<?, ?> actual, double delta, @Nullable boolean[] deltaApplied)
    {
        if (actual == expected)
        {
            return true;
        }

        if (actual.size() != expected.size())
        {
            return false;
        }

        try
        {
            for (Map.Entry<?, ?> e : expected.entrySet())
            {
                final Object key = e.getKey();
                final Object value = e.getValue();

                if (value == null)
                {
                    if (!(actual.get(key) == null && actual.containsKey(key)))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!AOAssertion.equals(value, actual.get(key), delta, deltaApplied))
                    {
                        return false;
                    }
                }
            }
        }
        catch (ClassCastException | NullPointerException unused)
        {
            return false;
        }

        return true;
    }

    public static boolean equals(ArrayList<?> expected, ArrayList<?> actual, double delta, @Nullable boolean[] deltaApplied)
    {
        if (actual == expected)
        {
            return true;
        }

        if (actual.size() != expected.size())
        {
            return false;
        }

        for (int ii = 0; ii < expected.size(); ii++)
        {
            if (!AOAssertion.equals(expected.get(ii), actual.get(ii), delta, deltaApplied))
            {
                return false;
            }
        }

        return true;
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
