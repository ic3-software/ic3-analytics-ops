package ic3.analyticsops.restapi.reply.table;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AORestApiTable
{
    @Nullable
    public AORestApiTableOptions options;

    @Nullable
    public AORestApiTableHeader header;

    @Nullable
    public List<AORestApiTableRow> rows;

    public boolean isEmpty()
    {
        return rows != null && rows.isEmpty();
    }

    public int rowCount()
    {
        if (rows != null)
        {
            return rows.size();
        }
        return 0;
    }

    public int getHeaderPos(String name)
    {
        if (header != null)
        {
            return header.getPos(name);
        }

        return -1;
    }

    @Nullable
    public AORestApiTableRowEx getRowEx(String header, String value)
    {
        if (rows != null)
        {
            final int headerPos = getHeaderPos(header);

            if (headerPos != -1)
            {
                for (AORestApiTableRow row : rows)
                {
                    if (value.equals(row.getValue(headerPos)))
                    {
                        return new AORestApiTableRowEx(this.header, row);
                    }
                }
            }
        }
        return null;
    }

    public void prettyPrint(PrintStream out)
    {
        final List<Integer> widths = setupWidths();

        if (header != null && header.headers != null && !header.headers.isEmpty())
        {
            prettyPrintHorizontalLine(out, widths);

            for (int ii = 0; ii < header.headers.size(); ii++)
            {
                if (ii > 0)
                {
                    out.print(" | ");
                }
                out.printf("%-" + widths.get(ii) + "s", header.headers.get(ii));
            }

            out.println();
        }

        if (rows != null)
        {
            prettyPrintHorizontalLine(out, widths);

            for (AORestApiTableRow row : rows)
            {
                final List<Object> cells = row.cells;

                if (cells != null && !cells.isEmpty())
                {
                    for (int ii = 0; ii < cells.size(); ii++)
                    {
                        if (ii > 0)
                        {
                            out.print(" | ");
                        }

                        final Object cell = cells.get(ii);
                        final String str = cell != null ? cell.toString() : "";

                        out.print(String.format("%-" + widths.get(ii) + "s", str));
                    }

                    out.println();
                }
            }

            prettyPrintHorizontalLine(out, widths);
        }

    }

    private void prettyPrintHorizontalLine(PrintStream out, List<Integer> widths)
    {
        if (header != null && header.headers != null && !header.headers.isEmpty())
        {
            for (int ii = 0; ii < header.headers.size(); ii++)
            {
                if (ii > 0)
                {
                    out.print("-|-");
                }
                out.printf("%-" + widths.get(ii) + "s", str('-', widths.get(ii)));
            }

            out.println();
        }
    }

    private String str(char cc, int len)
    {
        return String.valueOf(cc).repeat(Math.max(0, len));
    }

    private List<Integer> setupWidths()
    {
        final List<Integer> widths = new ArrayList<Integer>();

        if (header != null && header.headers != null)
        {
            for (String identifier : header.headers)
            {
                if (identifier != null)
                {
                    widths.add(identifier.length());
                }
            }
        }

        if (rows != null)
        {
            for (AORestApiTableRow row : rows)
            {
                final List<Object> cells = row.cells;

                if (cells != null)
                {
                    for (int ii = 0; ii < cells.size(); ii++)
                    {
                        final Object cell = cells.get(ii);
                        final String str = cell != null ? cell.toString() : "";

                        final int len = str.length();

                        if (len > widths.get(ii))
                        {
                            widths.set(ii, len);
                        }
                    }
                }
            }
        }

        return widths;
    }

}
