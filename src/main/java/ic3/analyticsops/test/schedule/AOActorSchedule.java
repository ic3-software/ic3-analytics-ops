package ic3.analyticsops.test.schedule;

import ic3.analyticsops.test.AOActor;

public class AOActorSchedule
{
    private final AOActor actor;

    private final boolean once;

    private final long startMS;

    private final long endMS;

    public AOActorSchedule(AOActor actor)
    {
        this.actor = actor;
        this.once = true;
        this.startMS = 0;
        this.endMS = 0;
    }

    public AOActorSchedule(AOActor actor, long startMS, long endMS)
    {
        this.actor = actor;
        this.once = false;
        this.startMS = startMS;
        this.endMS = endMS;
    }

    public AOActor getActor()
    {
        return actor;
    }

    public boolean isOnce()
    {
        return once;
    }

    /**
     * Relative to the actual start of the test.
     */
    public long getStartMS()
    {
        return startMS;
    }

    /**
     * Relative to the actual start of the test.
     */
    public long getEndMS()
    {
        return endMS;
    }

    @Override
    public String toString()
    {
        if (once)
        {
            return actor.getName();
        }
        else
        {
            return actor.getName() + "[" + startMS + "-" + endMS + "]";
        }
    }
}
