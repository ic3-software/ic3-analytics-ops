package ic3.analyticsops.restapi.reply.tabular;

import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

/**
 * A very much simplified view of a Tidy Table.
 */
public class AOTabularDataset
{
    public final int rowCount;

    public final int tidyMaxRowCount;

    public final boolean tidyMaxRowCountReached;

    public final List<AOTabularDataColumn> columns;

    public final boolean hasError;

    public AOTabularDataset(int rowCount, int tidyMaxRowCount, boolean tidyMaxRowCountReached, List<AOTabularDataColumn> columns)
    {
        this.rowCount = rowCount;
        this.tidyMaxRowCount = tidyMaxRowCount;
        this.tidyMaxRowCountReached = tidyMaxRowCountReached;
        this.columns = columns;
        this.hasError = columns.stream().anyMatch(c -> c.hasError);
    }

    public void prettyPrint(PrintStream out)
    {
        out.println("            row count : " + rowCount);
        out.println("        max row count : " + tidyMaxRowCount);
        out.println("max row count reached : " + tidyMaxRowCountReached);
        out.println("         column count : " + columns.size());

        prettyPrintRows(out);
    }

    public void prettyPrintRows(PrintStream out)
    {
        final int[] maxWidths = new int[columns.size()];

        for (int ii = 0; ii < maxWidths.length; ii++)
        {
            final AOTabularDataColumn column = columns.get(ii);

            maxWidths[ii] = column.prettyPrintMaxWidth(rowCount);
        }

        for (int rr = 0; rr < rowCount; rr++)
        {
            if (rr == 0)
            {
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.NAME);
            }

            prettyPrintRow(out, maxWidths, rr, null);
        }
    }

    protected void prettyPrintRow(PrintStream out, int[] maxWidths, int rr, @Nullable AORestApiMdxTidyTablePrettyPrinterHeader header)
    {
        final StringBuilder row = new StringBuilder();

        for (int cc = 0; cc < columns.size(); cc++)
        {
            final AOTabularDataColumn column = columns.get(cc);

            final String value = (header != null) ? column.prettyPrintHeader(header) : column.prettyPrint(rr);

            row.append(" | ").append(value);

            final int padding = maxWidths[cc] - value.length();
            row.append(" ".repeat(Math.max(0, padding)));
        }

        out.println(row);
    }

}
