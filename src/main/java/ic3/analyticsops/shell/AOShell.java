package ic3.analyticsops.shell;

import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
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
        }
        catch (Exception ex)
        {
            LOGGER.error("[shell] could not setup the system properties : exit(-1)", ex);
            System.exit(-1);
        }

        final AOTest test = createTest();

        if (test == null)
        {
            LOGGER.error("[shell] could not setup the test : exit(-1)");
            System.exit(-1);
        }

        final AOTestContext context = new AOTestContext(test);

        try
        {
            test.run(context);
        }
        catch (InterruptedException ex /* dunno yet */)
        {
            LOGGER.error("[shell] test interrupted : exit(-1)", ex);

            context.onTestInterrupted(ex);
        }
        finally
        {
            context.shutdown() /* safe */;
        }

        if (context.isOnError())
        {
            LOGGER.error("[shell] test completed on error : exit(-1)");
            System.exit(-1);
        }
        else
        {
            LOGGER.info("[shell] test completed successfully : exit(0)");
            System.exit(0);
        }
    }

    @Nullable
    public static AOTest createTest()
    {
        try
        {
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

            LOGGER.info("[shell] JSON configuration : {}", json5.getAbsolutePath());

            final AOTest test = AOTest.create(json5);

            test.validate() /* ensure as much as possible its JSON definition was valid */;

            return test;
        }
        catch (Exception ex)
        {
            LOGGER.error("[shell] could not create and validate the test", ex);
            return null;
        }
    }
}
