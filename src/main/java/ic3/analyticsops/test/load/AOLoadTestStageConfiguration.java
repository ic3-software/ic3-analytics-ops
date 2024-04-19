package ic3.analyticsops.test.load;

import ic3.analyticsops.test.AOActor;
import ic3.analyticsops.test.AOSerializable;
import ic3.analyticsops.test.AOTestValidationException;

import java.time.Duration;
import java.util.Map;

public class AOLoadTestStageConfiguration extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOLoadTestConfiguration jsonParentTestConfiguration;

    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient int jsonStageNb;

    private final Duration duration;

    /**
     * <pre>
     * actor name => expected number of actors at the end of the duration
     * </pre>
     */
    private final Map<String, Integer> targets;

    protected AOLoadTestStageConfiguration()
    {
        // JSON deserialization

        this.duration = null;
        this.targets = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOLoadTestConfiguration jsonParent, int jsonStateNb)
    {
        this.jsonParentTestConfiguration = jsonParent;
        this.jsonStageNb = jsonStateNb;
    }

    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField("load.stages[" + jsonStageNb + "].duration", duration);
        validateNonEmptyField("load.stages[" + jsonStageNb + "].targets", targets);

        for (Map.Entry<String, Integer> entry : targets.entrySet())
        {
            final String actorName = entry.getKey();

            final AOActor actor = jsonParentTestConfiguration.jsonParentTest.lookupActiveActor(actorName);

            if (actor == null)
            {
                throw new AOTestValidationException("load.stages[" + jsonStageNb + "].targets missing active actor from test : " + actorName);
            }
        }
    }

    public Duration getDuration()
    {
        return duration;
    }

}

