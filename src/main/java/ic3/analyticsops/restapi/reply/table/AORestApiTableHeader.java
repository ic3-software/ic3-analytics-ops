package ic3.analyticsops.restapi.reply.table;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AORestApiTableHeader
{
    @Nullable
    public List<String> headers;

    public int getPos(String name)
    {
        if (headers != null)
        {
            for (int ii = 0; ii < headers.size(); ii++)
            {
                final String header = headers.get(ii);

                if (name.equals(header))
                {
                    return ii;
                }
            }
        }
        return -1;
    }

    public String toString()
    {
        return headers != null ? headers.toString() : "";
    }
}
