package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOActor;
import ic3.analyticsops.test.AOActorContext;

public class AOActorSummary
{
    private final AOActor actor;

    private int runCount;

    private long elapsedMSavgSum;

    private long elapsedMSmax = Long.MIN_VALUE;

    private long elapsedMSmaxTS = 0;

    private long elapsedMSmin = Long.MAX_VALUE;

    private long elapsedMSminTS = 0;

    public AOActorSummary(AOActor actor)
    {
        this.actor = actor;
    }

    public AOActor getActor()
    {
        return actor;
    }

    public void addForContext(AOActorContext context)
    {
        runCount += context.getRunCount();

        if (context.getElapsedMSmax() > elapsedMSmax)
        {
            elapsedMSmax = context.getElapsedMSmax();
            elapsedMSmaxTS = context.getElapsedMSmaxTS();
        }

        if (context.getElapsedMSmin() < elapsedMSmin)
        {
            elapsedMSmin = context.getElapsedMSmin();
            elapsedMSminTS = context.getElapsedMSminTS();
        }

        elapsedMSavgSum += context.getElapsedMSavg() * context.getRunCount();
    }

    public int getRunCount()
    {
        return runCount;
    }

    public long getElapsedMSavg()
    {
        return elapsedMSavgSum / runCount;
    }

    public long getElapsedMSmax()
    {
        return elapsedMSmax;
    }

    public long getElapsedMSmaxTS()
    {
        return elapsedMSmaxTS;
    }

    public long getElapsedMSmin()
    {
        return elapsedMSmin;
    }

    public long getElapsedMSminTS()
    {
        return elapsedMSminTS;
    }
}
