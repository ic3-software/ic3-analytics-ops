package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.utils.AOLog4jUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AOBigBrother
{
    private final AOTestContext context;

    private final ReentrantLock shutdownLOCK = new ReentrantLock();

    private final Condition shutdownCondition = shutdownLOCK.newCondition();

    private final AtomicBoolean shutdown = new AtomicBoolean();

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
                    shutdownLOCK.lock();

                    try
                    {
                        shutdownCondition.await(1_000, TimeUnit.MILLISECONDS);
                    }
                    finally
                    {
                        shutdownLOCK.unlock();
                    }
                }
                catch (InterruptedException ignored)
                {
                }

                final double load = proc.getSystemCpuLoadBetweenTicks(prevTicks);
                prevTicks = proc.getSystemCpuLoadTicks();

                final Double failAtCpuLoad = context.getLoadFailAtCpuLoad();

                if (failAtCpuLoad != null && load >= failAtCpuLoad)
                {
                    AOLog4jUtils.BIG_BROTHER.error("CPU load failed : {} >= {}", load, failAtCpuLoad);
                    context.onLoadCpuLoadError();
                }

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

        shutdownLOCK.lock();

        try
        {
            shutdownCondition.signalAll();
        }
        finally
        {
            shutdownLOCK.unlock();
        }
    }

}
