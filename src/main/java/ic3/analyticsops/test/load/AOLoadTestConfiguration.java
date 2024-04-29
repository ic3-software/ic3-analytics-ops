package ic3.analyticsops.test.load;

import ic3.analyticsops.test.AOSerializable;
import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class AOLoadTestConfiguration extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOTest jsonParentTest;

    /**
     * The CPU load is checked every 'cpuLoadTick'. Defaulted to 1 sec.
     */
    @Nullable
    private final Duration cpuLoadTick;

    /**
     * The statistics are reported every 'statsTick'. Defaulted to 1 sec.
     */
    @Nullable
    private final Duration statsTick;

    /**
     * Optional max. (testing) system load before an error is generated (a value between 0 and 1).
     */
    @Nullable
    private final Double failAtCpuLoad;

    /**
     * Load test profiles for each actor.
     */
    private final List<AOLoadTestActorConfiguration> actors;

    protected AOLoadTestConfiguration()
    {
        // JSON deserialization

        this.cpuLoadTick = null;
        this.statsTick = null;
        this.failAtCpuLoad = null;
        this.actors = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOTest jsonParent)
    {
        this.jsonParentTest = jsonParent;

        if (actors != null /* before any validation */)
        {
            if (!actors.isEmpty() && actors.getLast() == null)
            {
                actors.removeLast() /* trailing comma in JSON5 [] */;
            }

            for (int tt = 0; tt < actors.size(); tt++)
            {
                final AOLoadTestActorConfiguration actor = actors.get(tt);
                actor.onFromJson(this, tt);
            }
        }
    }

    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField("load.actors", actors);

        boolean activeActorFound = false;

        for (AOLoadTestActorConfiguration actor : actors)
        {
            actor.validate();

            if (jsonParentTest.lookupActiveActor(actor.getActor()) != null)
            {
                activeActorFound = true;
            }
        }

        if (!activeActorFound)
        {
            throw new AOTestValidationException("load.actors does not reference any active actor in the test");
        }
    }

    @Nullable
    public Double getFailAtCpuLoad()
    {
        return failAtCpuLoad;
    }

    public List<AOLoadTestActorConfiguration> getActors()
    {
        return actors;
    }

    public long getCpuLoadTickMS()
    {
        return cpuLoadTick != null ? cpuLoadTick.toMillis() : 1_000;
    }

    public long getStatsTickMS()
    {
        return statsTick != null ? statsTick.toMillis() : 1_000;
    }
}
