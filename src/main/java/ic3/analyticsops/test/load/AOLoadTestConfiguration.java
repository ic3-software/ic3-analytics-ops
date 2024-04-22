package ic3.analyticsops.test.load;

import ic3.analyticsops.test.AOSerializable;
import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestValidationException;

import java.util.List;

public class AOLoadTestConfiguration extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOTest jsonParentTest;

    /**
     * Load test profiles for each actor.
     */
    private final List<AOLoadTestActorConfiguration> actors;

    protected AOLoadTestConfiguration()
    {
        // JSON deserialization

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

    public List<AOLoadTestActorConfiguration> getActors()
    {
        return actors;
    }

}
