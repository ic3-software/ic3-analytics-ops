package ic3.analyticsops.test.schedule;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class AOTestSchedule
{
    /**
     * No duration means each actor will run once their tasks.
     */
    @Nullable
    private final Duration duration;

    private final List<AOActorSchedule> actors;

    public AOTestSchedule(@Nullable Duration duration, List<AOActorSchedule> actors)
    {
        this.duration = duration;
        this.actors = actors;
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
