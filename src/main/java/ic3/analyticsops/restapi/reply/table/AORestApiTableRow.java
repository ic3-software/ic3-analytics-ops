package ic3.analyticsops.restapi.reply.table;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AORestApiTableRow
{
    @Nullable
    public List<Object> cells;

    @Nullable
    public Object getValue(int pos)
    {
        if (cells != null && pos > -1 && pos < cells.size())
        {
            return cells.get(pos);
        }
        return null;
    }

    public String toString()
    {
        return cells != null ? cells.toString() : "";
    }

}
