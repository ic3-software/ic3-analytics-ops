package ic3.analyticsops.restapi.reply.tidy.mdx;

import org.jetbrains.annotations.Nullable;

public class AORestApiMdxTidyTableColumnHierarchyInfo
{
    public String caption;

    public String name;

    public String uniqueName;

    /**
     * Dunno but date, time when the key of all the members of the axis are of type date or time (single key).
     */
    @Nullable
    public AORestApiMdxTidyTableMemberType type;

    @Nullable
    public AORestApiMdxTidyTableMemberInfo defaultMember;
}
