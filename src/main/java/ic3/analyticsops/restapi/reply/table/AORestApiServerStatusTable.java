package ic3.analyticsops.restapi.reply.table;

import com.vdurmont.semver4j.Semver;

public class AORestApiServerStatusTable extends AORestApiPropertyTable
{
    public int getPID()
    {
        final String pid = getValue("pid");
        return pid != null ? Integer.parseInt(pid) : 0;
    }

    public String getServerVersionEx()
    {
        final String version = getValue("icCubeServerVersion");
        return version != null ? version : "n/a";
    }

    public String getDashboardsVersionEx()
    {
        final String version = getValue("icCubeDashboardsVersion");
        return version != null ? version : "n/a";
    }

    public Semver getServerVersion()
    {
        final String version = getValue("icCubeServerVersion");

        if (version == null)
        {
            return null;
        }

        return new Semver(version, Semver.SemverType.LOOSE).withClearedBuild();
    }

    public Semver getDashboardsVersion()
    {
        final String version = getValue("icCubeDashboardsVersion");

        if (version == null)
        {
            return null;
        }

        return new Semver(version, Semver.SemverType.LOOSE).withClearedBuild();
    }
}
