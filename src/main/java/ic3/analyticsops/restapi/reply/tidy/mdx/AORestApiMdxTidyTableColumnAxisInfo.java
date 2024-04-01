package ic3.analyticsops.restapi.reply.tidy.mdx;

import org.jetbrains.annotations.Nullable;

public class AORestApiMdxTidyTableColumnAxisInfo
{
    public int axis;

    @Nullable
    public String role;

    /**
     * -1 if not relevant, axis 0 all tuples are in the same column.
     */
    public int hierarchyIdx;

    public AORestApiMdxTidyTableColumnHierarchyInfo hierarchyInfo;

    @Nullable
    public AORestApiMdxTidyTableColumnLevelInfo levelInfo;

    public AORestApiMdxTidyTableMemberType getType()
    {
        return levelInfo != null ? levelInfo.type : hierarchyInfo.type;
    }

    @Nullable
    public AORestApiMdxTidyTableMemberSubType getSubType()
    {
        return levelInfo != null ? levelInfo.subType : null;
    }
}
