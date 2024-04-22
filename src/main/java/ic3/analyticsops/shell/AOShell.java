package ic3.analyticsops.shell;

import ic3.analyticsops.test.AOActor;
import ic3.analyticsops.test.AOTest;
import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.test.load.AOLoadTestActorConfiguration;
import ic3.analyticsops.test.load.AOLoadTestConfiguration;
import ic3.analyticsops.test.schedule.AOActorSchedule;
import ic3.analyticsops.test.schedule.AOTestSchedule;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AOShell
{
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
            AOLog4jUtils.SHELL.error("[shell] could not setup the system properties : exit(-1)", ex);
            System.exit(-1);
        }

        final AOTest test = createTest();

        if (test == null)
        {
            AOLog4jUtils.SHELL.error("[shell] could not create the test : exit(-1)");
            System.exit(-1);
        }

        // A list of actor(s) to run for a given period of time (start - stop interval).
        final AOTestSchedule schedule = createTestSchedule(test);

        if (schedule == null)
        {
            AOLog4jUtils.SHELL.error("[shell] could not create the test schedule : exit(-1)");
            System.exit(-1);
        }

        final AOTestContext context = new AOTestContext(test, schedule);

        try
        {
            context.run();
        }
        catch (InterruptedException ex /* dunno yet */)
        {
            AOLog4jUtils.SHELL.error("[shell] test interrupted : exit(-1)", ex);

            context.onTestInterrupted(ex);
        }
        finally
        {
            context.shutdown() /* safe */;
        }

        if (context.isOnError())
        {
            AOLog4jUtils.SHELL.error("[shell] test completed on error : exit(-1)");
            System.exit(-1);
        }
        else
        {
            AOLog4jUtils.SHELL.info("[shell] test completed successfully : exit(0)");
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
                json5 = new File("etc/tests/demo.test.json5");
            }

            AOLog4jUtils.SHELL.info("[shell] JSON configuration : {}", json5.getAbsolutePath());

            final AOTest test = AOTest.create(json5);

            test.validate() /* ensure as much as possible its JSON definition was valid */;

            return test;
        }
        catch (Exception ex)
        {
            AOLog4jUtils.SHELL.error("[shell] could not create and validate the test", ex);
            return null;
        }
    }

    @Nullable
    public static AOTestSchedule createTestSchedule(AOTest test)
    {
        try
        {
            final AOLoadTestConfiguration load = test.getLoad();

            final List<AOActorSchedule> schedules;

            if (load != null)
            {
                schedules = createTestScheduleForLoadTesting(test);
            }
            else
            {
                schedules = createTestScheduleForRegularTesting(test);
            }

            return new AOTestSchedule(test.getDuration(), schedules);
        }
        catch (Exception ex)
        {
            AOLog4jUtils.SHELL.error("[shell] could not create and validate the test schedule", ex);
            return null;
        }
    }

    public static List<AOActorSchedule> createTestScheduleForRegularTesting(AOTest test)
    {
        final List<AOActorSchedule> schedules = new ArrayList<>();

        final Duration duration = test.getDuration();

        for (AOActor actor : test.activeActors())
        {
            if (duration == null)
            {
                schedules.add(new AOActorSchedule(actor));
            }
            else
            {
                schedules.add(new AOActorSchedule(actor, 0, duration.toMillis()));
            }
        }

        return schedules;
    }

    public static List<AOActorSchedule> createTestScheduleForLoadTesting(AOTest test)
    {
        final List<AOActorSchedule> schedules = new ArrayList<>();

        final AOLoadTestConfiguration load = test.getLoad();

        if (load == null)
        {
            throw new RuntimeException("internal error : unexpected missing load configuration");
        }

        for (AOLoadTestActorConfiguration actorConfiguration : load.getActors())
        {
            final AOActor actor = test.lookupActiveActor(actorConfiguration.getActor());

            if (actor != null)
            {
                final long delayMS = actorConfiguration.getDelayMS();

                final long rampUpMS = actorConfiguration.getRampUpMS();
                final long steadyStateMS = actorConfiguration.getSteadyStateMS();

                final long createStepMS = actorConfiguration.getRampUpMS() / actorConfiguration.getCount();
                final long killStepMS = actorConfiguration.getRampDownMS() / actorConfiguration.getCount();

                for (int ii = 0; ii < actorConfiguration.getCount(); ii++)
                {
                    schedules.add(new AOActorSchedule(
                            actor,
                            delayMS + createStepMS * ii,
                            delayMS + rampUpMS + steadyStateMS + killStepMS * ii
                    ));
                }
            }
        }

        return schedules;
    }

}
