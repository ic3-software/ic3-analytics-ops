package ic3.analyticsops.test.assertion;

import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

public class AOOpenReportAssertion extends AOAssertion
{
    /**
     * Nullable to ensure it has been defined by the JSON5 for sure.
     */
    @Nullable
    private final Boolean missing;

    protected AOOpenReportAssertion()
    {
        // JSON deserialization

        this.missing = null;
    }

    @Override
    public void validate()
            throws AOTestValidationException
    {
        super.validate();

        validateNonEmptyField(validateFieldPathPrefix() + "missing", missing);
    }

    public void assertOk(String reportPath, boolean nonExisting, boolean printReady)
    {
        if (missing == null /* should have been validated by now */)
        {
            throw new AssertionError("unexpected missing field 'missing'");
        }

        if (missing)
        {
            AOAssertion.assertTrue("report-not-existing:" + reportPath, nonExisting);
            AOAssertion.assertFalse("open-report:" + reportPath, printReady);
        }
        else
        {
            AOAssertion.assertFalse("report-not-existing:" + reportPath, nonExisting);
            AOAssertion.assertTrue("open-report:" + reportPath, printReady);
        }
    }

}
