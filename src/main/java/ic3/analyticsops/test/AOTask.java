package ic3.analyticsops.test;

import ic3.analyticsops.common.AOException;
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

    private final boolean dumpJson;

    /**
     * Dunno but GSON does not like the generic declaration.
     */
    @Nullable
    private final List<AOAssertion> assertions;

    protected AOTask()
    {
        // JSON deserialization

        this.name = null;
        this.dumpJson = false;
        this.assertions = null;
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

        if (!withAssertions())
        {
            validateEmptyField(validateFieldPathPrefix() + "assertions", assertions());
        }
        else
        {
            validateAssertions();
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

    /**
     * Requires some assertions being defined in the task definition (JSON5).
     */
    public abstract boolean withAssertions();

    public boolean isDumpJson()
    {
        return dumpJson;
    }

    protected List<ASSERTION> assertions()
    {
        return (List<ASSERTION>) assertions;
    }

    public abstract void run(AOTaskContext context)
            throws AOException;
}
