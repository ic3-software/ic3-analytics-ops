package ic3.analyticsops.test;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class AOPerformanceTargets extends AOSerializable
{
    /**
     * Asserted after each run of the task.
     */
    @Nullable
    private final Duration durationMax;

    /**
     * Asserted at the end of the test.
     */
    @Nullable
    private final Duration durationAverageEnd;

    protected AOPerformanceTargets()
    {
        // JSON deserialization

        durationMax = null;
        durationAverageEnd = null;
    }

    public void assertOk(AOTaskGauge gauge, boolean end)
    {
        if (!end)
        {
            if (durationMax != null)
            {
                final long actualMaxMS = gauge.getRunElapsedMSmax();
                final long expectedMaxMS = durationMax.toMillis();

                AOAssertion.assertTrue("[performance] duration-max " + actualMaxMS + " < " + expectedMaxMS, actualMaxMS <= expectedMaxMS);
            }
        }
        else
        {
            if (durationAverageEnd != null)
            {
                final long actualAverageMS = gauge.getRunElapsedMSavg();
                final long expectedAverageMS = durationAverageEnd.toMillis();

                AOAssertion.assertTrue("[performance] (ending) average " + actualAverageMS + " < " + expectedAverageMS, actualAverageMS <= expectedAverageMS);
            }
        }
    }

    // TODO : documentation + release notes
}
