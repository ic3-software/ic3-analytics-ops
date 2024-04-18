package ic3.analyticsops.test;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.common.AOPause;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AOTask<ASSERTION extends AOAssertion> extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOActor jsonParentActor;

    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient int jsonTaskNb;

    /**
     * When null, the kind will be used instead.
     */
    @Nullable
    private final String name;

    /**
     * Writes to the log the actual JSON returned by the server.
     */
    private final Boolean dumpJson;

    /**
     * Writes to the log a pretty-print version of the payload of the JSON reply.
     */
    private final Boolean dumpResult;

    /**
     * An optional pause applied after the processing of the task.
     */
    @Nullable
    private final AOPause pause;

    /**
     * Dunno but GSON does not like the generic declaration.
     */
    @Nullable
    private final List<AOAssertion> assertions;

    @Nullable
    private final AOPerformanceTargets performanceTargets;

    protected AOTask()
    {
        // JSON deserialization

        this.name = null;
        this.dumpJson = null;
        this.dumpResult = null;
        this.pause = null;
        this.assertions = null;
        this.performanceTargets = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOActor jsonParent, int jsonTaskNb)
    {
        this.jsonParentActor = jsonParent;
        this.jsonTaskNb = jsonTaskNb;

        if (assertions != null)
        {
            if (!assertions.isEmpty() && assertions.getLast() == null)
            {
                assertions.removeLast() /* trailing comma in JSON5 [] */;
            }

            for (int aa = 0; aa < assertions.size(); aa++)
            {
                final AOAssertion assertion = assertions.get(aa);
                assertion.onFromJson(this, aa);
            }
        }
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate()
            throws AOTestValidationException
    {
        validateProps();

        switch (getAssertionsMode())
        {
            case NONE ->
            {
                validateEmptyField(validateFieldPathPrefix() + "assertions", assertions);
            }
            case OPTIONAL ->
            {
                // nothing we can do right now : the task is taking care when using them.
            }
            case MANDATORY ->
            {
                validateAssertions();
            }
        }
    }

    public void validateProps()
            throws AOTestValidationException
    {
    }

    public void validateAssertions()
            throws AOTestValidationException
    {
        validateNonEmptyField(validateFieldPathPrefix() + "assertions", assertions);

        for (AOAssertion assertion : assertions)
        {
            assertion.validate();
        }
    }

    public String validateFieldPathPrefix()
    {
        return "actors[" + jsonParentActor.jsonActorNb + "].tasks[" + jsonTaskNb + "].";
    }

    public String getName()
    {
        return name != null ? name : getKind();
    }

    public abstract String getKind();

    public boolean isDumpJson()
    {
        return dumpJson != null ? dumpJson : jsonParentActor.isDumpJson();
    }

    public boolean isDumpResult()
    {
        return dumpResult != null ? dumpResult : jsonParentActor.isDumpResult();
    }

    @Nullable
    public Long getPauseMS()
    {
        return pause != null ? pause.pauseMS() : null;
    }

    public abstract AOAssertionMode getAssertionsMode();

    /**
     * Introduced for OpenReport that might have some an assertion.
     */
    public boolean withOptionalAssertions()
    {
        return false;
    }

    /**
     * Assuming (and enforced) not null.
     */
    protected List<ASSERTION> getAssertions()
    {
        if (assertions == null)
        {
            throw new RuntimeException("internal error: missing assertions");
        }
        return (List<ASSERTION>) assertions;
    }

    @Nullable
    protected List<ASSERTION> getOptionalAssertions()
    {
        return (List<ASSERTION>) assertions;
    }

    @Nullable
    public AOPerformanceTargets getPerformanceTargets()
    {
        return performanceTargets;
    }

    public abstract void run(AOTaskContext context)
            throws AOException;
}
