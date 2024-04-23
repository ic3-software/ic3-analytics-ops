package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOActor;
import ic3.analyticsops.test.AOActorContext;
import ic3.analyticsops.utils.AOLog4jUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Introduced for load-testing statistics.
 */
public class AOStats
{
    private int rowNb;

    private final Map<AOActor, Integer> positions = new HashMap<>();

    private final AOList<AOStatsColumn<?>> columns = new AOList<>();

    public AOStats()
    {
    }

    public void onTick(Map<AOActor, AtomicInteger> runningActors, List<AOActorContext> runningActorContexts)
    {
        synchronized (columns)
        {
            for (Map.Entry<AOActor, AtomicInteger> entry : runningActors.entrySet())
            {
                final AOActor actor = entry.getKey();
                final AtomicInteger running = entry.getValue();

                final int pos = positions.computeIfAbsent(actor, a -> positions.size());

                final AOIntegerStatsColumn countC = columns.computeIfAbsent(
                        pos * 2, () -> new AOIntegerStatsColumn(actor.getName() + "/Count")
                );

                countC.setIntValue(rowNb, running.get());
            }

            for (AOActorContext context : runningActorContexts)
            {
                final long ms = context.getElapsedMS();

                if (ms == -1)
                {
                    continue;
                }

                final AOActor actor = context.getActor();

                final int pos = positions.computeIfAbsent(actor, a -> positions.size());

                final AOAvgLongStatsColumn avgC = columns.computeIfAbsent(
                        pos * 2 + 1, () -> new AOAvgLongStatsColumn(actor.getName() + "/ElapsedMS")
                );

                avgC.setLongValue(rowNb, 1, ms);
            }

            rowNb++;
        }
    }

    public void dump()
    {
        synchronized (columns)
        {
            AOLog4jUtils.TEST.debug("[test] ticks:{}", rowNb);

            final int columnCount = columns.size();

            // Table Header
            {
                final StringBuilder csv = new StringBuilder("Tick");

                for (int cc = 0; cc < columnCount; cc++)
                {
                    final AOStatsColumn<?> column = columns.get(cc);

                    if (column == null)
                    {
                        continue;
                    }

                    csv.append(",").append(column.getName());
                }

                AOLog4jUtils.TEST.debug("[test] >  {}", csv.toString());
            }

            // Table Rows

            for (int tt = 0; tt < rowNb; tt++)
            {
                final StringBuilder csv = new StringBuilder(String.valueOf(tt));

                for (int cc = 0; cc < columnCount; cc++)
                {
                    final AOStatsColumn<?> column = columns.get(cc);

                    if (column == null)
                    {
                        continue;
                    }

                    final Object value = column.getValue(tt);

                    csv.append(",").append(value != null ? value.toString() : null);
                }

                AOLog4jUtils.TEST.debug("[test] >  {}", csv.toString());
            }
        }
    }
}
