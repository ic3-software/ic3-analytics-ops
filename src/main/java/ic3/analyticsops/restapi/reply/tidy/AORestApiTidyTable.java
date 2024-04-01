package ic3.analyticsops.restapi.reply.tidy;

import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public abstract class AORestApiTidyTable<COLUMN extends AORestApiTidyTableColumn>
{
    public String classID;

    public int rowCount;

    public int tidyMaxRowCount;

    public boolean tidyMaxRowCountReached;

    public boolean withPaging;

    public Map<String, String> query;

    @Nullable
    public Map<Integer, AORestApiTidyTable<?>> steps;

    public List<COLUMN> columns;

    /**
     * Add a transient reference to this table in each column.
     * Handy for MDX member column assert equals.
     */
    public void bindColumns()
    {
        if (columns != null)
        {
            for (COLUMN column : columns)
            {
                column.table = this;
                column.bindPages();
            }
        }
    }

    public int getRowCount()
    {
        return rowCount;
    }

    public int getMaxRowCount()
    {
        return tidyMaxRowCount;
    }

    public boolean isMaxRowCountReached()
    {
        return tidyMaxRowCountReached;
    }

    public int getColumCount()
    {
        return columns != null ? columns.size() : 0;
    }

    public COLUMN getColumnByName(String name)
    {
        if (columns != null)
        {
            for (COLUMN column : columns)
            {
                if (name.equals(column.name))
                {
                    return column;
                }
            }
        }
        return null;
    }

    public void prettyPrint(PrintStream out)
    {
        out.println("              classID : " + classID);
        out.println("            row count : " + rowCount);
        out.println("        max row count : " + tidyMaxRowCount);
        out.println("max row count reached : " + tidyMaxRowCountReached);
        out.println("          with paging : " + withPaging);

        if (steps != null)
        {
            out.println("                steps : " + steps.size());
        }

        if (rowCount > 0)
        {
            if (columns == null || columns.isEmpty())
            {
                out.println("         column count : 0");
            }
            else
            {
                out.println("         column count : " + columns.size());

                prettyPrintRows(out);
            }
        }

        if (steps != null)
        {
            for (Map.Entry<Integer, AORestApiTidyTable<?>> entry : steps.entrySet())
            {
                final Integer pos = entry.getKey();
                final AORestApiTidyTable<?> step = entry.getValue();

                out.println("                 step : " + pos);

                step.prettyPrint(out);
            }
        }
    }

    protected abstract void prettyPrintRows(PrintStream out);

    protected void prettyPrintRow(PrintStream out, int[] maxWidths, int rr, @Nullable AORestApiMdxTidyTablePrettyPrinterHeader header)
    {
        final StringBuilder row = new StringBuilder();

        for (int cc = 0; cc < columns.size(); cc++)
        {
            final AORestApiTidyTableColumn column = columns.get(cc);

            final String value = (header != null) ? column.prettyPrintHeader(header) : column.prettyPrint(rr);

            row.append(" | ").append(value);

            final int padding = maxWidths[cc] - value.length();
            row.append(" ".repeat(Math.max(0, padding)));
        }

        out.println(row);
    }

    public void assertEquals(AORestApiTidyTable<?> other)
    {
        assertPropsEquals(other);
        assertColumnsEquals(other);
        assertStepSEquals(other);
    }

    public void assertPropsEquals(AORestApiTidyTable<?> other)
    {
        AOAssertion.assertEquals("row count", getRowCount(), other.getRowCount());

        AOAssertion.assertEquals("max. row count", getMaxRowCount(), other.getMaxRowCount());
        AOAssertion.assertEquals("max. row count reached", isMaxRowCountReached(), other.isMaxRowCountReached());

        AOAssertion.assertEquals("paging", withPaging, other.withPaging);
    }

    public void assertColumnsEquals(AORestApiTidyTable<?> other)
    {
        AOAssertion.assertEquals("column count", getColumCount(), other.getColumCount());

        for (int cc = 0; cc < getColumCount(); cc++)
        {
            final AORestApiTidyTableColumn column = columns.get(cc);
            final AORestApiTidyTableColumn columnActual = other.columns.get(cc);

            column.assertEquals(columnActual, true);
        }
    }

    public void assertStepSEquals(AORestApiTidyTable<?> other)
    {
        final int stepCount = steps != null ? steps.size() : -1;
        final int stepCountActual = other.steps != null ? other.steps.size() : -1;

        AOAssertion.assertEquals("step count", stepCount, stepCountActual);

        if (stepCount > 0)
        {
            for (Map.Entry<Integer, AORestApiTidyTable<?>> entry : steps.entrySet())
            {
                final int stepNb = entry.getKey();

                final AORestApiTidyTable<?> stepTable = entry.getValue();
                final AORestApiTidyTable<?> stepTableActual = other.steps.get(stepNb);

                stepTable.assertEquals(stepTableActual);
            }
        }
    }

}
