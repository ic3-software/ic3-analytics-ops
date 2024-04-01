package ic3.analyticsops.restapi.reply.table;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AORestApiTableRowEx
{
    /**
     * Assuming no nulls anymore and header.headers.size == row.cells.size.
     */
    public final AORestApiTableHeader header;

    /**
     * Assuming no nulls anymore and header.headers.size == row.cells.size.
     */
    public final AORestApiTableRow row;

    public AORestApiTableRowEx(AORestApiTableHeader header, AORestApiTableRow row)
    {
        this.header = header;
        this.row = row;
    }

    @Nullable
    public <V> V getValue(String name)
    {
        final int headerPos = header.getPos(name);
        return (V) row.getValue(headerPos);
    }

    public void prettyPrint(PrintStream out)
    {
        int maxWidth = 0;

        for (int ii = 0; ii < header.headers.size(); ii++)
        {
            final String h = header.headers.get(ii);
            maxWidth = Math.max(maxWidth, h.length());
        }

        for (int ii = 0; ii < header.headers.size(); ii++)
        {
            final String h = header.headers.get(ii);
            final Object v = row.cells.get(ii);

            out.printf("%" + maxWidth + "s : %s%n", h, v);
        }
    }
}
