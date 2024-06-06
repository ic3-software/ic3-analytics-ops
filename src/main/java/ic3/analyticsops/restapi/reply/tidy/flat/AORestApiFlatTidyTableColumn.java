package ic3.analyticsops.restapi.reply.tidy.flat;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumnNaN;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;
import ic3.analyticsops.test.AOAssertion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AORestApiFlatTidyTableColumn extends AORestApiTidyTableColumn
{
    private List<Object> values;

    @Nullable
    private AORestApiTidyTableColumnNaN valuesN;

    public AORestApiFlatTidyTable getTable()
    {
        return (AORestApiFlatTidyTable) table;
    }

    public int getRowCount()
    {
        return values != null ? values.size() : 0;
    }

    @Nullable
    public Object getValue(int rowIndex)
    {
        final Object value = values.get(rowIndex);

        if (value == null && valuesN != null)
        {
            return valuesN.get(rowIndex) /* null or NaN, +Infinity, -Infinity */;
        }

        return value;
    }

    @Nullable
    public Object getTabularDatasetValue(int rowIndex)
    {
        return getValue(rowIndex);
    }

    @Override
    public int prettyPrintMaxWidth()
    {
        final AORestApiFlatTidyTable table = getTable();

        int max = 0;

        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.NAME).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.TYPE).length());

        for (int rr = 0; rr < table.rowCount; rr++)
        {
            max = Math.max(max, prettyPrint(rr).length());
        }

        return max;
    }

    @Override
    public String prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader header)
    {
        return switch (header)
        {
            case NAME -> name + "[" + caption + "]";
            case TYPE -> " T: " + type.toString();
            case SEPARATOR -> "--";
            default -> "";
        };
    }

    @Override
    public String prettyPrint(int rowIndex)
    {
        final Object value = getValue(rowIndex);

        if (value == null)
        {
            return "<empty>";
        }

        return value.toString();
    }

    @Override
    public void assertEquals(AORestApiTidyTableColumn other, boolean valueOnly, double delta)
    {
        super.assertEquals(other, valueOnly, delta);

        final AORestApiFlatTidyTable table = getTable();

        final int rowCount = table.rowCount;

        AOAssertion.assertEquals("column[" + name + "] row/column size", rowCount, values.size());

        final AORestApiFlatTidyTableColumn actual = (AORestApiFlatTidyTableColumn) other;

        AOAssertion.assertEquals("column[" + name + "] size", getRowCount(), actual.getRowCount());

        for (int rr = 0; rr < rowCount; rr++)
        {
            final Object value = getValue(rr);
            final Object valueActual = actual.getValue(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] value", value, valueActual, delta);
        }
    }
}
