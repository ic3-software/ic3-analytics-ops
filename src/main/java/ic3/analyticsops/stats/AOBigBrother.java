package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.utils.AOLog4jUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.concurrent.atomic.AtomicBoolean;

public class AOBigBrother
{
    private final AOTestContext context;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public AOBigBrother(AOTestContext context)
    {
        this.context = context;
    }

    public void start()
    {
        new Thread(() ->
        {
            final SystemInfo si = new SystemInfo();
            final HardwareAbstractionLayer hw = si.getHardware();
            final CentralProcessor proc = hw.getProcessor();

            long[] prevTicks = proc.getSystemCpuLoadTicks();

            while (!shutdown.get())
            {
                try
                {
                    Thread.sleep(1_000);
                }
                catch (InterruptedException ignored)
                {
                }

                final double load = proc.getSystemCpuLoadBetweenTicks(prevTicks);
                prevTicks = proc.getSystemCpuLoadTicks();

                if (load >= .8)
                {
                    AOLog4jUtils.BIG_BROTHER.warn("CPU load : {}", String.format("%.1f", load * 100));
                }
            }

        }, "big-brother").start();
    }

    public void shutdown()
    {
        shutdown.set(true);
    }

}
