package ic3.analyticsops.test.schedule;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class AOTestSchedule
{
    /**
     * Load-testing : actors are duplicated and run according to a given profile.
     */
    private final boolean loadTesting;

    /**
     * No duration means each actor will run once their tasks unless this is a load test.
     */
    @Nullable
    private final Duration duration;

    private final List<AOActorSchedule> actors;

    public AOTestSchedule(boolean loadTesting, @Nullable Duration duration, List<AOActorSchedule> actors)
    {
        this.loadTesting = loadTesting;
        this.duration = duration;
        this.actors = actors;
    }

    public boolean isLoadTesting()
    {
        return loadTesting;
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

    public long getActorMaxDurationMS()
    {
        long max = Integer.MIN_VALUE;

        for (AOActorSchedule actor : actors)
        {
            max = Math.max(max, actor.getDurationMS());
        }

        return max;
    }
}
