package ic3.analyticsops.restapi.reply.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AORestApiMdxResult
{
    @Nullable
    public AORestApiMdxError error;

    /**
     * RestApiMdxTidyTable | RestApiDrillthroughTidyTable | RestApiFlatTidyTable
     */
    @Nullable
    public AORestApiTidyTable<?> dataSet;

    public void prettyPrint(PrintStream out)
    {
        if (error != null)
        {
            error.prettyPrint(out);
        }

        if (dataSet != null)
        {
            dataSet.prettyPrint(out);
        }
    }
}
