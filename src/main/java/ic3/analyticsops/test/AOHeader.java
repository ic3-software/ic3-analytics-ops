package ic3.analyticsops.test;

public class AOHeader extends AOSerializable
{
    public final String name;

    public final String value;

    protected AOHeader()
    {
        // JSON deserialization

        this.name = null;
        this.value = null;
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate(String prefix)
            throws AOTestValidationException
    {
        validateNonEmptyField(prefix + "name", name);
        validateNonEmptyField(prefix + "value", value);
    }
}
