package ic3.analyticsops.restapi.reply.tidy;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AORestApiTidyTableColumnNaN
{
    public Set<Integer> NaN;

    public Set<Integer> InfinityP;

    public Set<Integer> InfinityN;

    @Nullable
    public Double get(int pos)
    {
        if (NaN != null && NaN.contains(pos))
        {
            return Double.NaN;
        }
        if (InfinityP != null && InfinityP.contains(pos))
        {
            return Double.POSITIVE_INFINITY;
        }
        if (InfinityN != null && InfinityN.contains(pos))
        {
            return Double.NEGATIVE_INFINITY;
        }
        return null;
    }

}
