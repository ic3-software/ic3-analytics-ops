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

    private final List<AOLoadTestStageConfiguration> stages;

    protected AOLoadTestConfiguration()
    {
        // JSON deserialization

        this.stages = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOTest jsonParent)
    {
        this.jsonParentTest = jsonParent;

        if (stages != null /* before any validation */)
        {
            if (!stages.isEmpty() && stages.getLast() == null)
            {
                stages.removeLast() /* trailing comma in JSON5 [] */;
            }

            for (int tt = 0; tt < stages.size(); tt++)
            {
                final AOLoadTestStageConfiguration stage = stages.get(tt);
                stage.onFromJson(this, tt);
            }
        }
    }

    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField("load.stages", stages);

        for (AOLoadTestStageConfiguration stage : stages)
        {
            stage.validate();
        }
    }

}
