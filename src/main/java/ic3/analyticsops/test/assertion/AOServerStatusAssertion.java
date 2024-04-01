package ic3.analyticsops.test.assertion;

import com.vdurmont.semver4j.Semver;
import ic3.analyticsops.restapi.reply.table.AORestApiServerStatusTable;
import org.jetbrains.annotations.Nullable;

public class AOServerStatusAssertion extends AOAssertion
{
    /**
     * Full version string (e.g., with timestamp).
     */
    @Nullable
    private String serverVersionEx;

    @Nullable
    private String serverVersion;

    /**
     * Full version string (e.g., with timestamp).
     */
    @Nullable
    private String dashboardsVersionEx;

    @Nullable
    private String dashboardsVersion;

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
