package ic3.analyticsops.restapi.reply.mdx;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

public class AORestApiMdxScriptResult
{
    /**
     * Used for generated expected non-regression expected results.
     */
    @Nullable
    public transient String json;

    @Nullable
    public AORestApiMdxError error;

    @Nullable
    public List<AORestApiMdxResult> results;

    public void prettyPrint(PrintStream out)
    {
        if (error != null)
        {
            out.println("MDX ERROR");
            error.prettyPrint(out);
        }

        if (results != null)
        {
            for (int ii = 0; ii < results.size(); ii++)
            {
                final AORestApiMdxResult result = results.get(ii);

                out.println("MDX RESULT:" + ii);
                result.prettyPrint(out);
            }
        }
    }

}
