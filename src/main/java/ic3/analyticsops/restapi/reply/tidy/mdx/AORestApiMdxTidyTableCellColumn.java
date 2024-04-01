package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumnNaN;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AORestApiMdxTidyTableCellColumn extends AORestApiMdxTidyTableColumn
{
    /**
     * axis0 position.
     */
    private int tupleIdx;

    /**
     * MDX cell property VALUE.
     */
    private List<Object> values;

    @Nullable
    private AORestApiTidyTableColumnNaN valuesN;

    /**
     * MDX cell property VALUE.
     */
    @Nullable
    private List<AORestApiMdxTidyTableCellError> errors;

    /**
     * MDX cell property FORMATTED_VALUE_EX => tidy table property formattedValue.
     */
    @Nullable
    private List<String> formattedValues;

    /**
     * MDX cell property BACK_COLOR => tidy table property mdxCellBackColor.
     */
    @Nullable
    private List<String> backColors;

    /**
     * MDX cell property FORE_COLOR => tidy table property mdxCellForeColor.
     */
    @Nullable
    private List<String> foreColors;

    /**
     * The .fs property (FORMAT_STRING) of the corresponding member in the axis0 (dunno when tuple w/ multiple formats).
     */
    @Nullable
    private String fs;

    /**
     * The .color property of the corresponding member in the axis0
     */
    @Nullable
    private String color;

    /**
     * Introduced to fix the .color property (new field does not break existing client w/ new server).
     */
    @Nullable
    private String[] colors;

    /**
     * From the paging axis: 1, ... N member. 0 being the 'default' values of this column.
     * <p>
     * When the last axis is #measures, it is converted to 'pages' as a way to return a vector of measures
     * for each cell (converted into cell column's properties in the client). Select w/ more than 2 axes.
     */
    @Nullable
    private AORestApiMdxTidyTableCellPage[] pages;

    @Override
    public void bindPages()
    {
        if (pages != null)
        {
            for (AORestApiMdxTidyTableCellPage page : pages)
            {
                if (page != null)
                {
                    page.column = this;
                }
            }
        }
    }

    public int getRowCount()
    {
        return values != null ? values.size() : 0;
    }

    @Nullable
    public Object getValue(int rowIndex)
    {
        if (values == null)
        {
            return null;
        }

        final Object value = values.get(rowIndex);

        if (value == null && valuesN != null)
        {
            return valuesN.get(rowIndex) /* null or NaN, +Infinity, -Infinity */;
        }

        return value;
    }

    @Nullable
    public String getFormattedValue(int rowIndex)
    {
        return formattedValues != null ? formattedValues.get(rowIndex) : null;
    }

    @Nullable
    public AORestApiMdxTidyTableCellError getError(int rowIndex)
    {
        return errors != null ? errors.get(rowIndex) : null;
    }

    @Nullable
    public String getErrorCode(int rowIndex)
    {
        final AORestApiMdxTidyTableCellError error = getError(rowIndex);

        if (error != null)
        {
            return error.errorCode;
        }

        return null;
    }

    @Nullable
    public String getBackColor(int rowIndex)
    {
        return backColors != null && rowIndex < backColors.size() ? backColors.get(rowIndex) : null;
    }

    @Nullable
    public String getForeColor(int rowIndex)
    {
        return foreColors != null && rowIndex < foreColors.size() ? foreColors.get(rowIndex) : null;
    }

    @Nullable
    public String getColor(int rowIndex)
    {
        return colors != null && rowIndex < colors.length ? colors[rowIndex] : null;
    }

    @Override
    public int prettyPrintMaxWidth()
    {
        final AORestApiMdxTidyTable table = getTable();

        int max = 0;

        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.NAME).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.TYPE).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.SUB_TYPE).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.FORMAT_STRING).length());

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
            case NAME -> prettyPrintHeader(page ->
            {
                if (page == 0)
                {
                    return name + "[" + caption + "]";
                }

                final AORestApiMdxTidyTableCellPage p = pages[page - 1];
                return p.name + "[" + p.caption + "]";
            });
            case TYPE -> " T: " + prettyPrintHeader(page ->
            {
                if (page == 0)
                {
                    return type.toString();
                }

                final AORestApiMdxTidyTableCellPage p = pages[page - 1];
                return p.type.toString();
            });
            case SUB_TYPE -> "SB: " + prettyPrintHeader(page ->
            {
                if (page == 0)
                {
                    return typeParam != null ? typeParam.toString() : "";
                }

                final AORestApiMdxTidyTableCellPage p = pages[page - 1];
                return (p.typeParam != null ? p.typeParam.toString() : "");
            });
            case FORMAT_STRING -> "FS: " + (fs != null ? fs : "");
            case SEPARATOR -> "--";
        };
    }

    private String prettyPrintHeader(PrettyPrinterHeaderValue value)
    {
        final StringBuilder name = new StringBuilder(value.get(0));

        if (pages != null)
        {
            name.append(" ( ");

            for (int pp = 0; pp < pages.length; pp++)
            {
                if (pp > 1)
                {
                    name.append(", ");
                }
                name.append(value.get(pp + 1));
            }

            name.append(" )");
        }

        return name.toString();
    }

    @Override
    public String prettyPrint(int rowIndex)
    {
        final StringBuilder row = new StringBuilder(prettyPrintValueOrError(rowIndex));

        final AORestApiMdxTidyTableCellPage[] thisPages = pages;

        if (thisPages != null)
        {
            row.append(" ( ");

            for (int pp = 0; pp < thisPages.length; pp++)
            {
                if (pp > 0)
                {
                    row.append(", ");
                }
                row.append(thisPages[pp].prettyPrintValueOrError(rowIndex));
            }

            row.append(" )");
        }

        return row.toString();
    }

    private String prettyPrintValueOrError(int rowIndex)
    {
        if (errors != null)
        {
            final AORestApiMdxTidyTableCellError error = errors.get(rowIndex);

            if (error != null)
            {
                return error.errorCode;
            }
        }

        final Object value = getValue(rowIndex);

        if (value == null)
        {
            return "<empty>";
        }

        return value.toString();
    }

    @FunctionalInterface
    interface PrettyPrinterHeaderValue
    {
        String get(int page);
    }

    @Override
    public void assertEquals(AORestApiTidyTableColumn other, boolean valueOnly, double delta)
    {
        super.assertEquals(other, valueOnly, delta);

        final AORestApiMdxTidyTable table = getTable();

        final int rowCount = table.rowCount;

        AOAssertion.assertEquals("row/column size", rowCount, values.size());

        final AORestApiMdxTidyTableCellColumn actual = (AORestApiMdxTidyTableCellColumn) other;

        AOAssertion.assertEquals("column size", getRowCount(), actual.getRowCount());

        for (int rr = 0; rr < rowCount; rr++)
        {
            final Object value = getValue(rr);
            final Object valueActual = actual.getValue(rr);

            final boolean deltaApplied = AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] value", value, valueActual, delta);

            if (!deltaApplied)
            {
                // -----------------------------------------------------------------------------------------------------
                // See AORestApiMdxTidyTableCellPage as well for same kind of logic.
                //      should we parse and compare the formatted value ?
                //          (not sure always possible)
                // -----------------------------------------------------------------------------------------------------

                final Object formattedValue = getFormattedValue(rr);
                final Object formattedValueActual = actual.getFormattedValue(rr);

                AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] formatted-value", formattedValue, formattedValueActual);
            }

            final String errorCode = getErrorCode(rr);
            final Object errorCodeActual = actual.getErrorCode(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] error-code", errorCode, errorCodeActual);

            final String backColor = getBackColor(rr);
            final String backColorActual = actual.getBackColor(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] back-color", backColor, backColorActual);

            final String foreColor = getForeColor(rr);
            final String foreColorActual = actual.getForeColor(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] fore-color", foreColor, foreColorActual);

            if (!valueOnly /* dunno */)
            {
                final String color = getColor(rr);
                final String colorActual = actual.getColor(rr);

                AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] color", color, colorActual);
            }
        }

        if (!valueOnly)
        {
            AOAssertion.assertEquals("column[" + name + "] formatted-string", fs, actual.fs);
            AOAssertion.assertEquals("column[" + name + "] color", color, actual.color);
        }

        final int pageCount = pages != null ? pages.length : 0;
        final int pageCountActual = actual.pages != null ? actual.pages.length : 0;

        AOAssertion.assertEquals("column[" + name + "] page-count", pageCount, pageCountActual);

        for (int pp = 0; pp < pageCount; pp++)
        {
            final AORestApiMdxTidyTableCellPage page = pages[pp];
            final AORestApiMdxTidyTableCellPage pageActual = actual.pages[pp];

            page.assertEquals("column[" + name + "] page[" + pp + "]", pageActual, delta);
        }
    }
}

