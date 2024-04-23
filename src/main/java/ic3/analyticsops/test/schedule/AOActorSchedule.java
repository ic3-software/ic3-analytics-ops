package ic3.analyticsops.test.schedule;

import ic3.analyticsops.test.AOActor;

public class AOActorSchedule
{
    private final AOActor actor;

    /**
     * To better identify an instance of a given actor when duplicated (e.g., thread name).
     */
    private final int id;

    private final boolean once;

    private final long startMS;

    private final long endMS;

    private final long durationMS;

    public AOActorSchedule(AOActor actor)
    {
        this.actor = actor;
        this.id = -1;

        this.once = true;

        this.startMS = 0;
        this.endMS = 0;
        this.durationMS = 0;
    }

    public AOActorSchedule(AOActor actor, long startMS, long endMS)
    {
        this.actor = actor;
        this.id = -1;

        this.once = false;

        this.startMS = startMS;
        this.endMS = endMS;
        this.durationMS = endMS - startMS;
    }

    public AOActorSchedule(AOActor actor, int id, long startMS, long endMS)
    {
        this.actor = actor;
        this.id = id;

        this.once = false;

        this.startMS = startMS;
        this.endMS = endMS;
        this.durationMS = endMS - startMS;
    }

    public AOActor getActor()
    {
        return actor;
    }

    public int getId()
    {
        return id;
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

    public long getDurationMS()
    {
        return durationMS;
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
