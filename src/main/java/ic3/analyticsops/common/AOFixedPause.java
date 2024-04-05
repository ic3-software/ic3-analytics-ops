package ic3.analyticsops.common;

import java.time.Duration;

public class AOFixedPause extends AOPause
{
    private final Duration fixed;

    private final long fixedMS;

    public AOFixedPause(Duration fixed)
    {
        this.fixed = fixed;
        this.fixedMS = fixed.toMillis();
    }

    @Override
    public long pauseMS()
    {
        return fixedMS;
    }

    @Override
    public String toString()
    {
        return fixed.toString() /* DO NOT CHANGE : JSON serialization */;
    }
}
