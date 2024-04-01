package ic3.analyticsops.restapi.reply.mdx;

public class AORestApiJavaNativeError
{
    private int lineNumber;

    private int startPosition;

    private int endPosition;

    private String message;

    @Override
    public String toString()
    {
        return lineNumber + "[" + startPosition + ":" + endPosition + "] " + message;
    }
}
