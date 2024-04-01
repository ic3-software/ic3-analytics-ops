package ic3.analyticsops.shell;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.utils.AOLog4jUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class AOShell
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        AOLog4jUtils.configure(Level.DEBUG);

        try
        {
            // ---------------------------------------------------------------------------------------------------------
            // Very early code demonstrating a test and its various tasks, using the definition from the
            // /etc/tests folder (current working directory being the root of the project).
            // ---------------------------------------------------------------------------------------------------------

            final AOTest test = AOTest.create(new File("etc/tests/smoke.test.json5"));

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
