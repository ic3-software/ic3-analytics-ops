package ic3.analyticsops.test.assertion;

import com.vdurmont.semver4j.Semver;
import ic3.analyticsops.restapi.reply.table.AORestApiServerStatusTable;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

public class AOServerStatusAssertion extends AOAssertion
{
    /**
     * Full version string (e.g., with timestamp).
     */
    @Nullable
    private final String serverVersionEx;

    @Nullable
    private final String serverVersion;

    /**
     * Full version string (e.g., with timestamp).
     */
    @Nullable
    private final String dashboardsVersionEx;

    @Nullable
    private final String dashboardsVersion;

    protected AOServerStatusAssertion()
    {
        // JSON deserialization

        this.serverVersionEx = null;
        this.serverVersion = null;
        this.dashboardsVersionEx = null;
        this.dashboardsVersion = null;
    }

    @Override
    public void validate()
            throws AOTestValidationException
    {
        super.validate();

        validateNonEmptyFields(validateFieldPathPrefix() + "serverVersionEx|serverVersion|dashboardsVersionEx|dashboardsVersion",
                serverVersionEx,
                serverVersion,
                dashboardsVersionEx,
                dashboardsVersion
        );
    }

    public void assertOk(AORestApiServerStatusTable actualStatus)
    {
        if (serverVersionEx != null)
        {
            final String actualServerVersionEx = actualStatus.getServerVersionEx();

            AOAssertion.assertEquals("server-version-ex", serverVersionEx, actualServerVersionEx);
        }

        if (dashboardsVersionEx != null)
        {
            final String actualDashboardsVersionEx = actualStatus.getDashboardsVersionEx();

            AOAssertion.assertEquals("dashboards-version-ex", dashboardsVersionEx, actualDashboardsVersionEx);
        }

        if (serverVersion != null)
        {
            final Semver serverVersion = new Semver(this.serverVersion, Semver.SemverType.LOOSE);
            final Semver actualServerVersion = actualStatus.getServerVersion();

            AOAssertion.assertEquals("server-version", serverVersion, actualServerVersion);
        }

        if (dashboardsVersion != null)
        {
            final Semver dashboardsVersion = new Semver(this.dashboardsVersion, Semver.SemverType.LOOSE);
            final Semver actualDashboardsVersion = actualStatus.getDashboardsVersion();

            AOAssertion.assertEquals("dashboards-version", dashboardsVersion, actualDashboardsVersion);
        }
    }

}
