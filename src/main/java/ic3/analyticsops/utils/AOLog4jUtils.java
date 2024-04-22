package ic3.analyticsops.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public abstract class AOLog4jUtils
{
    public static final Logger SHELL = LogManager.getLogger("AnalyticsOps.Shell");

    public static final Logger BIG_BROTHER = LogManager.getLogger("AnalyticsOps.BigBrother");

    public static final Logger CHROME = LogManager.getLogger("AnalyticsOps.Chrome");

    public static final Logger TEST = LogManager.getLogger("AnalyticsOps.Test");

    public static final Logger ACTOR = LogManager.getLogger("AnalyticsOps.Actor");

    public static final Logger DUMP_JSON = LogManager.getLogger("AnalyticsOps.DumpJson");

    public static final Logger PRETTY_PRINT = LogManager.getLogger("AnalyticsOps.PrettyPrint");

    private AOLog4jUtils()
    {
    }

    public static void configure(Level level)
    {
        final BuiltConfiguration configuration = createConfiguration(level);

        Configurator.reconfigure(configuration);

        final org.apache.log4j.Level warn = org.apache.log4j.Level.WARN;

        // CDP4j
        {
            org.apache.log4j.Logger.getLogger("cdp4j.launcher").setLevel(warn);
            org.apache.log4j.Logger.getLogger("cdp4j.flow").setLevel(warn);
            org.apache.log4j.Logger.getLogger("cdp4j.ws.request").setLevel(warn);
            org.apache.log4j.Logger.getLogger("cdp4j.ws.response").setLevel(warn);
        }
        // OSHI
        {
            org.apache.log4j.Logger.getLogger("oshi.util.FileUtil").setLevel(warn);
        }
    }

    public static BuiltConfiguration createConfiguration(Level threshold)
    {
        final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setConfigurationName("analytics-ops-console");

        // Console Appender
        final AppenderComponentBuilder console = builder.newAppender("Console", "Console")
                .addAttribute("target", "SYSTEM_OUT")
                .add(
                        builder.newLayout("PatternLayout")
                                .addAttribute("pattern", "{%40.40c} [%20.20t] [%5.5p] (%d{HH:mm:ss.SSS z}) %m%n")
                        // .addAttribute("pattern", "[%20.20t] [%5.5p] (%d{HH:mm:ss.SSS z}) %m%n")
                );

        // Root Logger
        final RootLoggerComponentBuilder root = builder.newRootLogger(threshold)
                .add(builder.newAppenderRef(console.getName()));

        builder.add(console);
        builder.add(root);

        return builder.build();
    }

}
