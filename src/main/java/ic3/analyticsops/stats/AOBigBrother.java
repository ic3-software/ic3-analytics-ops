package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOTestContext;
import ic3.analyticsops.test.schedule.AOTestSchedule;
import ic3.analyticsops.utils.AOLog4jUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Timer;
import java.util.TimerTask;

public class AOBigBrother
{
    private final AOTestContext context;

    private final Timer timer;

    private final TimerTask taskCpuLoad;

    private final TimerTask taskStats;

    public AOBigBrother(AOTestContext context)
    {
        this.context = context;

        this.timer = new Timer("big-brother");

        this.taskCpuLoad = new CpuLoadTask(context);
        this.taskStats = new StatsTask(context);
    }

    public void start()
    {
        try
        {
            final long ms = System.currentTimeMillis() % 1000;
            Thread.sleep(1000 - ms);
        }
        catch (InterruptedException ignored)
        {
        }

        final AOTestSchedule schedule = context.getSchedule();

        timer.scheduleAtFixedRate(taskCpuLoad, 0, schedule.getLoadTestingCpuLoadTickMS());
        timer.scheduleAtFixedRate(taskStats, 0, schedule.getLoadTestingStatsTickMS());
    }

    public void shutdown()
    {
        taskCpuLoad.cancel();
        taskStats.cancel();
    }

    static class CpuLoadTask extends TimerTask
    {
        private final AOTestContext context;

        private final CentralProcessor proc;

        private long[] prevTicks;

        public CpuLoadTask(AOTestContext context)
        {
            this.context = context;

            final SystemInfo si = new SystemInfo();
            final HardwareAbstractionLayer hw = si.getHardware();

            this.proc = hw.getProcessor();
            this.prevTicks = proc.getSystemCpuLoadTicks();
        }

        @Override
        public void run()
        {
            try
            {
                runUnsafe();
            }
            catch (Exception ex)
            {
                AOLog4jUtils.BIG_BROTHER.error("[big-brother] CPU load unexpected error", ex);
                context.setOnError();
            }
        }

        private void runUnsafe()
        {
            final double load = proc.getSystemCpuLoadBetweenTicks(prevTicks);
            prevTicks = proc.getSystemCpuLoadTicks();

            final Double failAtCpuLoad = context.getLoadFailAtCpuLoad();

            if (failAtCpuLoad != null && load >= failAtCpuLoad)
            {
                AOLog4jUtils.BIG_BROTHER.error("[big-brother] CPU load failed : {} >= {}", load, failAtCpuLoad);
                context.onLoadCpuLoadError();
            }

            if (load >= .8)
            {
                AOLog4jUtils.BIG_BROTHER.warn("[big-brother] CPU load : {}", String.format("%.1f", load * 100));
            }
        }
    }

    static class StatsTask extends TimerTask
    {
        private final AOTestContext context;

        public StatsTask(AOTestContext context)
        {
            this.context = context;
        }

        @Override
        public void run()
        {
            try
            {
                runUnsafe();
            }
            catch (Exception ex)
            {
                AOLog4jUtils.BIG_BROTHER.error("[big-brother] stats. unexpected error", ex);
                context.setOnError();
            }
        }

        private void runUnsafe()
        {
            context.onStatisticsTick();
        }
    }
}
