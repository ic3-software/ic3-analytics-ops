package ic3.analyticsops.test.schedule;

import ic3.analyticsops.test.load.AOLoadTestActorConfiguration;
import ic3.analyticsops.test.load.AOLoadTestConfiguration;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class AOTestSchedule
{
    /**
     * Load-testing : actors are duplicated and run according to a given profile.
     */
    @Nullable
    private final AOLoadTestConfiguration loadTesting;

    /**
     * No duration means each actor will run once their tasks unless this is a load test.
     */
    @Nullable
    private final Duration duration;

    private final List<AOActorSchedule> actors;

    public AOTestSchedule(@Nullable AOLoadTestConfiguration loadTesting, @Nullable Duration duration, List<AOActorSchedule> actors)
    {
        this.loadTesting = loadTesting;
        this.duration = duration;
        this.actors = actors;
    }

    public boolean isLoadTesting()
    {
        return loadTesting != null;
    }

    public long getLoadTestingMaxDurationMS()
    {
        long max = Integer.MIN_VALUE;

        if (loadTesting != null)
        {
            for (AOLoadTestActorConfiguration actor : loadTesting.getActors())
            {
                max = Math.max(max, actor.getDurationMS());
            }
        }

        return max;
    }

    @Nullable
    public Duration getDuration()
    {
        return duration;
    }

    public int getActorCount()
    {
        return actors.size();
    }

    public List<AOActorSchedule> getActors()
    {
        return actors;
    }
}
