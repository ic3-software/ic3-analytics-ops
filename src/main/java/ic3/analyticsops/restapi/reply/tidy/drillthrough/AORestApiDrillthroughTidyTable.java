package ic3.analyticsops.restapi.reply.tidy.drillthrough;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;
import ic3.analyticsops.test.AOAssertion;

import java.io.PrintStream;

public class AORestApiDrillthroughTidyTable extends AORestApiTidyTable<AORestApiDrillthroughTidyTableColumn>
{
    @Override
    protected void prettyPrintRows(PrintStream out)
    {
        final int[] maxWidths = new int[columns.size()];

        for (int ii = 0; ii < maxWidths.length; ii++)
        {
            final AORestApiDrillthroughTidyTableColumn column = columns.get(ii);

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

    @Override
    public void assertColumnsEquals(AORestApiTidyTable<?> other, double delta)
    {
        AOAssertion.assertEquals("column count", getColumCount(), other.getColumCount());

        for (int cc = 0; cc < getColumCount(); cc++)
        {
            final AORestApiTidyTableColumn column = columns.get(cc);

            // It looks like DRILLTHROUGH does not serialize columns in a determinist order.
            final AORestApiTidyTableColumn columnActual = other.getColumnByName(column.getName());

            column.assertEquals(columnActual, true, delta);
        }
    }
}

