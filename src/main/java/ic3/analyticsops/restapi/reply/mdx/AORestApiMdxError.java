package ic3.analyticsops.restapi.reply.mdx;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AORestApiMdxError
{
    public String errorCode;

    public String errorMessage;

    @Nullable
    public AORestApiMdxRange range;

    @Nullable
    public AORestApiJavaNativeErrors javaNativeErrors;

    public AORestApiMdxError(String errorCode, String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString()
    {
        return errorCode + " : " + errorMessage;
    }

    public void prettyPrint(PrintStream out)
    {
        out.println(errorCode + " : " + errorMessage);
    }
}
