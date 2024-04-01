package ic3.analyticsops.restapi.reply.tidy.flat;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;

import java.io.PrintStream;

public class AORestApiFlatTidyTable extends AORestApiTidyTable<AORestApiFlatTidyTableColumn>
{
    @Override
    protected void prettyPrintRows(PrintStream out)
    {
        final int[] maxWidths = new int[columns.size()];

        for (int ii = 0; ii < maxWidths.length; ii++)
        {
            final AORestApiFlatTidyTableColumn column = columns.get(ii);

            maxWidths[ii] = column.prettyPrintMaxWidth();
        }

        for (int rr = 0; rr < rowCount; rr++)
        {
            if (rr == 0)
            {
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.NAME);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.SEPARATOR);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.TYPE);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.SEPARATOR);
            }

            prettyPrintRow(out, maxWidths, rr, null);
        }
    }

}

