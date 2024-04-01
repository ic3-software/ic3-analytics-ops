package ic3.analyticsops.restapi.reply.mdx;

public class AORestApiMdxRangeRegion
{
    /**
     * 0-based.
     */
    public int from;

    /**
     * 0-based : inclusive.
     */
    public int to;

    /**
     * 1-based (from line number)
     */
    public int lineNumber;

    @Override
    public String toString()
    {
        return lineNumber + "[" + from + ":" + to + "]";
    }
}
