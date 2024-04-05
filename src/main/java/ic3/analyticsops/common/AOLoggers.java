package ic3.analyticsops.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AOLoggers
{
    private AOLoggers()
    {
    }

    public static final Logger SHELL = LogManager.getLogger("AnalyticsOps.Shell");

    public static final Logger CHROME = LogManager.getLogger("AnalyticsOps.Chrome");

    public static final Logger TEST = LogManager.getLogger("AnalyticsOps.Test");

    public static final Logger ACTOR = LogManager.getLogger("AnalyticsOps.Actor");

    public static final Logger DUMP_JSON = LogManager.getLogger("AnalyticsOps.DumpJson");

    public static final Logger PRETTY_PRINT = LogManager.getLogger("AnalyticsOps.PrettyPrint");

}
