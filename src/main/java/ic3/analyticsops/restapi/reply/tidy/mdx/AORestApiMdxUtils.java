package ic3.analyticsops.restapi.reply.tidy.mdx;

import java.util.regex.Pattern;

public abstract class AORestApiMdxUtils
{
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\]");

    private AORestApiMdxUtils()
    {
    }

    public static String escape(String name)
    {
        return ESCAPE_PATTERN.matcher(name).replaceAll("\\]\\]");
    }

}
