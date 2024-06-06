package ic3.analyticsops.restapi.reply.tabular;

import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;

import java.util.List;

public class AOTabularDataColumn
{
    public final String name;

    public final String caption;

    public final List<Object> values;

    public final boolean hasError;

    public AOTabularDataColumn(String name, String caption, List<Object> values, boolean hasError)
    {
        this.name = name;
        this.caption = caption;
        this.values = values;
        this.hasError = hasError;
    }

    public int prettyPrintMaxWidth(int rowCount)
    {
        int max = 0;

        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.CAPTION).length());

        for (int rr = 0; rr < rowCount; rr++)
        {
            max = Math.max(max, prettyPrint(rr).length());
        }

        return max;
    }

    public String prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader header)
    {
        return switch (header)
        {
            case NAME -> name;
            case CAPTION -> caption;
            default -> "";
        };
    }

    public String prettyPrint(int rowIndex)
    {
        final Object value = values.get(rowIndex);

        if (value == null)
        {
            return "";
        }

        return value.toString();
    }


}
