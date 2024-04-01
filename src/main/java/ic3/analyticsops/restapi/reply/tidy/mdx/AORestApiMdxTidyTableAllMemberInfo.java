package ic3.analyticsops.restapi.reply.tidy.mdx;

import org.jetbrains.annotations.Nullable;

public class AORestApiMdxTidyTableAllMemberInfo
{
    /**
     * The index as used both in 'memberInfoIndices' and in 'tupleIndexToMemberIndex'.
     */
    public int index;

    public String name;

    public String caption;

    public String uniqueName;

    public int childrenCount;

    @Nullable
    public Object key;

    public AORestApiMdxTidyTableMembersInfoLevel level;

    @Nullable
    public String formatString;
}
