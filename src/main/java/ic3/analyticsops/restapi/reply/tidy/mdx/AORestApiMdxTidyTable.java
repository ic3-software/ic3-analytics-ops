package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AORestApiMdxTidyTable extends AORestApiTidyTable<AORestApiMdxTidyTableColumn>
{
    public List<AORestApiMdxTidyTableAxis> axes;

    /**
     * [axis-index][hierarchy-index]
     */
    public AORestApiMdxTidyTableMembersInfo[][] infos;

    @Nullable
    public List<AORestApiMdxTidyTableMemberInfo> initialMembers;

    public int getAxisCount()
    {
        return axes.size();
    }

    public AORestApiMdxTidyTableAxis getAxis(int axis)
    {
        return axes.get(axis);
    }

    public AORestApiMdxTidyTableMembersInfo getMembers(int axis, int hierarchy)
    {
        return infos[axis][hierarchy];
    }

    public List<AORestApiMdxTidyTableCellColumn> getCellColumns()
    {
        final List<AORestApiMdxTidyTableCellColumn> cellColumns = new ArrayList<>();

        if (columns != null)
        {
            for (AORestApiMdxTidyTableColumn column : columns)
            {
                if (column instanceof AORestApiMdxTidyTableCellColumn cellColumn)
                {
                    cellColumns.add(cellColumn);
                }
            }
        }

        return cellColumns;
    }

    @Override
    protected void prettyPrintRows(PrintStream out)
    {
        final int[] maxWidths = new int[columns.size()];

        for (int ii = 0; ii < maxWidths.length; ii++)
        {
            final AORestApiMdxTidyTableColumn column = columns.get(ii);

            maxWidths[ii] = column.prettyPrintMaxWidth();
        }

        for (int rr = 0; rr < rowCount; rr++)
        {
            if (rr == 0)
            {
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.NAME);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.SEPARATOR);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.TYPE);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.SUB_TYPE);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.FORMAT_STRING);
                prettyPrintRow(out, maxWidths, rr, AORestApiMdxTidyTablePrettyPrinterHeader.SEPARATOR);
            }

            prettyPrintRow(out, maxWidths, rr, null);
        }
    }

    @Override
    public void assertEquals(AORestApiTidyTable<?> other, double delta)
    {
        super.assertEquals(other, delta);

        final AORestApiMdxTidyTable actual = (AORestApiMdxTidyTable) other;

        AOAssertion.assertEquals("axis count", getAxisCount(), actual.getAxisCount());
    }

    public void assertCellEquals(AORestApiMdxTidyTable other, double delta)
    {
        assertPropsEquals(other);

        // Cells columns only and ignore their names/captions.

        final List<AORestApiMdxTidyTableCellColumn> columns = getCellColumns();
        final List<AORestApiMdxTidyTableCellColumn> columnsActual = other.getCellColumns();

        AOAssertion.assertEquals("cell-column count", columns.size(), columnsActual.size());

        for (int cc = 0; cc < columns.size(); cc++)
        {
            final AORestApiTidyTableColumn column = columns.get(cc);
            final AORestApiTidyTableColumn columnActual = columnsActual.get(cc);

            column.assertEquals(columnActual, true, delta);
        }
    }
}
