package ic3.analyticsops.restapi.reply.tidy;

import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTablePrettyPrinterHeader;
import ic3.analyticsops.test.assertion.AOAssertion;

public abstract class AORestApiTidyTableColumn
{
    protected transient AORestApiTidyTable<?> table;

    private String classID;

    protected AORestApiTidyTableEntityType type;

    protected String name;

    protected String caption;

    public void bindPages()
    {
    }

    public String getName()
    {
        return name;
    }

    public abstract int prettyPrintMaxWidth();

    public abstract String prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader header);

    public abstract String prettyPrint(int rowIndex);

    public void assertEquals(AORestApiTidyTableColumn other, boolean valueOnly, double delta)
    {
        AOAssertion.assertEquals("column[" + name + "] type", type, other.type);

        if (!valueOnly)
        {
            AOAssertion.assertEquals("column[" + name + "] name", name, other.name);
            AOAssertion.assertEquals("column[" + name + "] caption", caption, other.caption);
        }
    }
}
