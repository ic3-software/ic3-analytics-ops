package ic3.analyticsops.common;

import java.time.Duration;
import java.util.Random;

public class AORandomPause extends AOPause
{
    private final Duration randomMin;

    private final long randomMinMS;

    private final Duration randomMax;

    private final long randomMaxMS;

    public AORandomPause(Duration first, Duration second)
    {
        final long firstMS = first.toMillis();
        final long secondMS = second.toMillis();

        if (firstMS < secondMS)
        {
            this.randomMin = first;
            this.randomMinMS = firstMS;

            this.randomMax = second;
            this.randomMaxMS = secondMS;
        }
        else
        {
            this.randomMin = second;
            this.randomMinMS = secondMS;

            this.randomMax = first;
            this.randomMaxMS = firstMS;
        }
    }

    @Override
    public long pauseMS()
    {
        // Adding 1 to ensure not zero (error) : this makes both the min. and max. value inclusive.
        return randomMinMS + new Random().nextLong(randomMaxMS - randomMinMS + 1);
    }

    @Override
    public String toString()
    {
        return randomMin.toString() + ":" + randomMax.toString() /* DO NOT CHANGE : JSON serialization */;
    }

}
