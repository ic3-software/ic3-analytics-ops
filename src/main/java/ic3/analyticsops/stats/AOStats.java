package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOActor;
import ic3.analyticsops.utils.AOLog4jUtils;

import java.util.Comparator;
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

    private final Map<AOActor, AOStatsColumn> runningActorCountColumns = new HashMap<>();

    public AOStats()
    {
    }

    public void onTick(Map<AOActor, AtomicInteger> runningActors)
    {
        // TODO [load-testing] would be nice to see as well tasks : elapsed-avg | elapsed-max  | elapsed-min

        // Create a tidy-table : [ tidy-column ]
        //
        //      MDX Player |  Dashboard Opener |  Schema Loader | ...
        //               1 |                 - |              - | ...
        //               2 |                 - |              1 | ...
        //               3 |                 2 |              - | ...
        //               3 |                 4 |              1 | ...
        //               3 |                 4 |              - | ...

        synchronized (runningActorCountColumns)
        {
            for (Map.Entry<AOActor, AtomicInteger> entry : runningActors.entrySet())
            {
                final AOActor actor = entry.getKey();
                final AtomicInteger running = entry.getValue();

                final AOStatsColumn column = runningActorCountColumns.computeIfAbsent(
                        actor, a -> new AOStatsColumn(actor, "Running Count")
                );

                column.addRow(rowNb, running.get());
            }

            rowNb++;
        }
    }

    public void dump()
    {
        synchronized (runningActorCountColumns)
        {
            final List<AOActor> actors = runningActorCountColumns.keySet().stream()
                    .sorted(Comparator.comparing(AOActor::getName))
                    .toList();

            final StringBuilder csv = new StringBuilder();

            for (int aa = 0; aa < actors.size(); aa++)
            {
                final AOActor actor = actors.get(aa);

                if (aa > 0)
                {
                    csv.append(",");
                }

                csv.append(actor.getName());
            }

            csv.append("\n");

            for (int tt = 0; tt < rowNb; tt++)
            {
                for (int aa = 0; aa < actors.size(); aa++)
                {
                    final AOActor actor = actors.get(aa);

                    if (aa > 0)
                    {
                        csv.append(",");
                    }

                    csv.append(runningActorCountColumns.get(actor).getValue(tt));
                }

                csv.append("\n");
            }

            AOLog4jUtils.TEST.debug("[test] ticks:{}\n{}", rowNb, csv.toString());
        }
    }
}
