package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableEntityType;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

public abstract class AORestApiMdxTidyTableColumn extends AORestApiTidyTableColumn
{
    @Nullable
    private String role;

    @Nullable
    protected AORestApiTidyTableEntityType typeParam;

    protected AORestApiMdxTidyTableColumnAxisInfo axis;

    public AORestApiMdxTidyTable getTable()
    {
        return (AORestApiMdxTidyTable) table;
    }

    @Override
    public abstract int prettyPrintMaxWidth();

    @Override
    public abstract String prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader header);

    @Override
    public abstract String prettyPrint(int rowIndex);

    public void assertEquals(AORestApiTidyTableColumn other, boolean valueOnly)
    {
        super.assertEquals(other, valueOnly);

        final AORestApiMdxTidyTableColumn actual = (AORestApiMdxTidyTableColumn) other;

        AOAssertion.assertEquals("column[" + name + "] type-param", typeParam, actual.typeParam);
    }

}
