package ic3.analyticsops.shell;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AOShell
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        AOLog4jUtils.configure(Level.DEBUG);

        try
        {
            final File props = new File(".env.dev");

            if (props.exists())
            {
                final Properties p = new Properties();

                p.putAll(System.getProperties());
                p.load(new FileReader(props, StandardCharsets.UTF_8));

                System.setProperties(p);
            }

            final String prop = System.getProperty("analytics.ops.test");

            final File json5;

            if (AOStringUtils.isNotEmpty(prop))
            {
                json5 = new File(prop);
            }
            else
            {
                json5 = new File("etc/tests/smoke.test.json5");
            }

            final AOTest test = AOTest.create(json5);

            final AOTestContext context = new AOTestContext(test);

            try
            {
                context.run();
            }
            finally
            {
                context.shutdown();
            }

            System.exit(0);
        }
        catch (AORestApiException | AOChromeException | IOException ex)
        {
            LOGGER.error("failed", ex);

            System.exit(-1);
        }
    }
}
