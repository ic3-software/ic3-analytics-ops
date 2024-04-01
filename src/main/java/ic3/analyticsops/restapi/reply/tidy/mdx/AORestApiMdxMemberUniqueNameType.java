package ic3.analyticsops.restapi.reply.tidy.mdx;

public enum AORestApiMdxMemberUniqueNameType
{
    /**
     * Generated from : level unique name + key.
     */
    LEVEL_KEY(0),
    /**
     * Generated from : level unique name + name.
     */
    LEVEL_NAME(1),
    /**
     * Generated from : parent unique name + key.
     */
    PARENT_KEY(2),
    /**
     * Generated from : parent unique name + name.
     */
    PARENT_NAME(3),
    X_ALL(4),
    X_CALC_DEFINED(5),
    X_CALC_MEASURE(6),
    X_MEASURE(7),
    X_OTHER(8),
    ;

    private final int tidyTableValue;

    AORestApiMdxMemberUniqueNameType(int tidyTableValue)
    {
        this.tidyTableValue = tidyTableValue;
    }

    public static AORestApiMdxMemberUniqueNameType fromTidyTableValue(int tidyTableValue)
    {
        return switch (tidyTableValue)
        {
            case 0 -> AORestApiMdxMemberUniqueNameType.LEVEL_KEY;
            case 1 -> AORestApiMdxMemberUniqueNameType.LEVEL_NAME;
            case 2 -> AORestApiMdxMemberUniqueNameType.PARENT_KEY;
            case 3 -> AORestApiMdxMemberUniqueNameType.PARENT_NAME;
            case 4 -> AORestApiMdxMemberUniqueNameType.X_ALL;
            case 5 -> AORestApiMdxMemberUniqueNameType.X_CALC_DEFINED;
            case 6 -> AORestApiMdxMemberUniqueNameType.X_CALC_MEASURE;
            case 7 -> AORestApiMdxMemberUniqueNameType.X_MEASURE;
            case 8 -> AORestApiMdxMemberUniqueNameType.X_OTHER;

            default -> throw new IllegalArgumentException("unexpected tidy type : " + tidyTableValue);
        };
    }

    public int getTidyTableValue()
    {
        return tidyTableValue;
    }
}
