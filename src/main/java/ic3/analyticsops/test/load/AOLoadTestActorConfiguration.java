package ic3.analyticsops.test.load;

import ic3.analyticsops.test.AOSerializable;
import ic3.analyticsops.test.AOTestValidationException;

import java.time.Duration;

public class AOLoadTestActorConfiguration extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOLoadTestConfiguration jsonParentTestConfiguration;

    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient int jsonActorNb;

    private final String actor;

    /**
     * The number of actor (threads) in the steady-state stage.
     */
    private final Integer count;

    /**
     * Optional initial delay before the ramp-up stage is starting.
     */
    private final Duration delay;

    /**
     * How long is the ramp-up stage : each actor is created every 'ramp-up / count' millis.
     */
    private final Duration rampUp;

    /**
     * How long is the steady-state stage : all actors are running their tasks for this period of time.
     */
    private final Duration steadyState;

    /**
     * How long is the ramp-down stage : each actor is stopped every 'ramp-down / count' millis.
     */
    private final Duration rampDown;

    protected AOLoadTestActorConfiguration()
    {
        // JSON deserialization

        this.actor = null;
        this.count = null;
        this.delay = null;
        this.rampUp = null;
        this.steadyState = null;
        this.rampDown = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOLoadTestConfiguration jsonParent, int jsonActorNb)
    {
        this.jsonParentTestConfiguration = jsonParent;
        this.jsonActorNb = jsonActorNb;
    }

    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField("load.actors[" + jsonActorNb + "].actor", actor);
        validateNonEmptyField("load.actors[" + jsonActorNb + "].count", count);
        validateNonEmptyField("load.actors[" + jsonActorNb + "].rampUp", rampUp);
        validateNonEmptyField("load.actors[" + jsonActorNb + "].steadyState", steadyState);
        validateNonEmptyField("load.actors[" + jsonActorNb + "].rampDown", rampDown);

        if (jsonParentTestConfiguration.jsonParentTest.lookupActor(actor) == null)
        {
            throw new AOTestValidationException("load.actors[" + jsonActorNb + "].actor the referenced actor is not defined in the test : " + actor);
        }
    }

    public String getActor()
    {
        return actor;
    }

    public int getCount()
    {
        return count != null ? count : 0;
    }

    public long getDelayMS()
    {
        return delay != null ? delay.toMillis() : 0;
    }

    public long getRampUpMS()
    {
        return rampUp != null ? rampUp.toMillis() : 0;
    }

    public long getSteadyStateMS()
    {
        return steadyState != null ? steadyState.toMillis() : 0;
    }

    public long getRampDownMS()
    {
        return rampDown != null ? rampDown.toMillis() : 0;
    }

    public long getDurationMS()
    {
        return getDelayMS() + getRampUpMS() + getSteadyStateMS() + getRampDownMS();
    }
}

