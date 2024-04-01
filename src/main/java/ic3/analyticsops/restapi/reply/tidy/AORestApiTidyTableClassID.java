package ic3.analyticsops.restapi.reply.tidy;

public enum AORestApiTidyTableClassID
{
    /**
     * SQL select.
     */
    SQL_TABLE,
    /**
     * SQL select: column
     */
    SQL_DATA,
    /**
     * E.g., result of a post-processing.
     */
    TIDY_TABLE,
    /**
     * Tidy column: column
     */
    TIDY_DATA,
    /**
     * MDX select.
     */
    MDX_TABLE,
    /**
     * MDX select: members column.
     */
    MDX_MEMBERS,
    /**
     * MDX select: members column.
     */
    MDX_CELLS,
    /**
     * MDX drillthrough.
     */
    MDX_DRILLTHROUGH_TABLE,
    /**
     * MDX drillthrough: column
     */
    MDX_DRILLTHROUGH_DATA,
    /**
     * MDX script info.
     */
    MDX_INFO_TABLE,
    /**
     * MDX script info: column
     */
    MDX_INFO_DATA,
}
