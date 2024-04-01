package ic3.analyticsops.test;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.assertion.AOAssertion;
import ic3.analyticsops.test.task.reporting.AOChromeException;

import java.util.List;

public abstract class AOTask<ASSERTION extends AOAssertion>
{
    private String name;

    private boolean dumpJson;

    /**
     * Dunno but GSON does not like the generic declaration.
     */
    private List<AOAssertion> assertions;

    public String getName()
    {
        return name != null ? name : getKind();
    }

    public abstract String getKind();

    public boolean isDumpJson()
    {
        return dumpJson;
    }

    protected List<ASSERTION> assertions()
    {
        return (List<ASSERTION>) assertions;
    }

    public abstract void run(AOTaskContext context)
            throws AORestApiException,
                   AOChromeException;
}
