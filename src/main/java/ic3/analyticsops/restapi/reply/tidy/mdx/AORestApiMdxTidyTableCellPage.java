package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumnNaN;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableEntityType;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * When the last axis is #measures, it is converted to 'pages' as a way to return a vector of measures
 * for each cell (converted into cell column's properties in the client). Select w/ more than 2 axes.
 * <pre>
 *    select
 *
 *      [Year].[Year].[2010] on 0
 *      [Customers].[Geography].[Region] on 1
 *      { [Measures].[Amount], [Measures].[Amount MIN], [Measures].[Amount MAX] } on "#MEASURES"
 *                    ^^                   ^^                       ^^
 *                    cell value           cell page 0              cell page 1
 *
 *    from [Sales]
 * </pre>
 */
public class AORestApiMdxTidyTableCellPage
{
    protected transient AORestApiMdxTidyTableCellColumn column;

    public String name;

    public String caption;

    public AORestApiTidyTableEntityType type;

    @Nullable
    public AORestApiTidyTableEntityType typeParam;

    /**
     * MDX cell property VALUE.
     */
    public List<Object> values;

    @Nullable
    public AORestApiTidyTableColumnNaN valuesN;

    /**
     * MDX cell property VALUE.
     */
    @Nullable
    public List<AORestApiMdxTidyTableCellError> errors;

    /**
     * MDX cell property FORMATTED_VALUE_EX => tidy table property formattedValue.
     */
    @Nullable
    public List<String> formattedValues;

    /**
     * MDX cell property BACK_COLOR => tidy table property mdxCellBackColor.
     */
    @Nullable
    public List<String> backColors;

    /**
     * MDX cell property FORE_COLOR => tidy table property mdxCellForeColor.
     */
    @Nullable
    public List<String> foreColors;

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

    public String prettyPrintValueOrError(int rowIndex)
    {
        if (errors != null)
        {
            final AORestApiMdxTidyTableCellError error = errors.get(rowIndex);

            if (error != null)
            {
                return error.errorCode;
            }
        }

        final Object value = values.get(rowIndex);

        if (value == null)
        {
            return "<empty>";
        }

        return value.toString();
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

    public void assertEquals(String message, AORestApiMdxTidyTableCellPage actual)
    {
        AOAssertion.assertEquals(message + " name", name, actual.name);
        AOAssertion.assertEquals(message + " caption", caption, actual.caption);
        AOAssertion.assertEquals(message + " type", type, actual.type);
        AOAssertion.assertEquals(message + " type-param", typeParam, actual.typeParam);

        final int rowCount = column.getTable().getRowCount();

        AOAssertion.assertEquals(message + " row/page size", rowCount, values.size());

        AOAssertion.assertEquals(message + " page size", getRowCount(), actual.getRowCount());

        for (int rr = 0; rr < rowCount; rr++)
        {
            final Object value = getValue(rr);
            final Object valueActual = actual.getValue(rr);

            AOAssertion.assertEquals(message + " page[" + name + "] row[" + rr + "] value", value, valueActual);

            final Object formattedValue = getFormattedValue(rr);
            final Object formattedValueActual = actual.getFormattedValue(rr);

            AOAssertion.assertEquals(message + " page[" + name + "] row[" + rr + "] formatted-value", formattedValue, formattedValueActual);

            final String errorCode = getErrorCode(rr);
            final Object errorCodeActual = actual.getErrorCode(rr);

            AOAssertion.assertEquals(message + " page[" + name + "] row[" + rr + "] error-code", errorCode, errorCodeActual);

            final String backColor = getBackColor(rr);
            final String backColorActual = actual.getBackColor(rr);

            AOAssertion.assertEquals(message + " page[" + name + "] row[" + rr + "] back-color", backColor, backColorActual);

            final String foreColor = getForeColor(rr);
            final String foreColorActual = actual.getForeColor(rr);

            AOAssertion.assertEquals(message + " page[" + name + "] row[" + rr + "] fore-color", foreColor, foreColorActual);
        }
    }
}
