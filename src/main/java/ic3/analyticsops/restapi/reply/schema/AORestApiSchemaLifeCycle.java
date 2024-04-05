package ic3.analyticsops.restapi.reply.schema;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

public class AORestApiSchemaLifeCycle
{
    public String schemaName;

    public AORestApiSchemaLoadStatus status;

    /**
     * E.g., the generated backup timestamp.
     */
    @Nullable
    public String info;

    @Nullable
    public List<String> errors;

    public void prettyPrint(PrintStream out)
    {
        out.println(schemaName + " : " + status);
    }
}
