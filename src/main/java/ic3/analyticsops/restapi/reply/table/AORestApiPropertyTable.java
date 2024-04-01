package ic3.analyticsops.restapi.reply.table;

import org.jetbrains.annotations.Nullable;

public class AORestApiPropertyTable extends AORestApiTable
{
    @Nullable
    public <V> V getValue(String name)
    {
        if (rows != null)
        {
            for (AORestApiTableRow row : rows)
            {
                if (row.cells != null)
                {
                    if (name.equals(row.cells.get(0)))
                    {
                        return (V) row.cells.get(1);
                    }
                }
            }
        }

        return null;
    }
}
