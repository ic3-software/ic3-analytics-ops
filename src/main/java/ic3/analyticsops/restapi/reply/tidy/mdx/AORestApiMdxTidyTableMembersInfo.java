package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumnNaN;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AORestApiMdxTidyTableMembersInfo
{
    public int axis;

    public boolean hasNonEmpty;

    public int hierarchyIdx;

    /**
     * The position of the member in the xyz (e.g., names) arrays for each index of the tuples in the axis.
     * <p>
     * The client code is calling that one: tupleIndexToMemberIndex.
     *
     * <pre>
     * private content(rowIdx: number): number {
     *
     *     const axis = this.getAxis();
     *     const tupleIdx = axis.rowIndexToTupleIndex[rowIdx];               -- axis.content
     *
     *     const members = this.getMdxAxisMemberInfo();
     *     const memberInfoIdx = members.tupleIndexToMemberIndex[tupleIdx];  -- members.memberInfoIndices
     *
     *     return memberInfoIdx;
     * }
     * </pre>
     */
    public List<Integer> memberInfoIndices;

    /**
     * All members are from the same level and contains the all member. This information overrides every
     * compressed array : e.g., uniqueNames, childrenCounts, etc...
     */
    @Nullable
    public AORestApiMdxTidyTableAllMemberInfo allMember;

    public List<String> names;

    /**
     * Null means same as names : use the names field instead.
     */
    @Nullable
    public List<String> captions;

    /**
     * String or generator-function. A single value means same value for all the members + allMember override.
     *
     * @see #getKey(int)
     */
    public List<?> uniqueNames;

    /**
     * A single value means same value for all the members + allMember override.
     */
    public List<Integer> childrenCounts;

    /**
     * A single value means same value for all the members + allMember override.
     */
    public List<String> levelCaptions;

    /**
     * A single value means same value for all the members + allMember override.
     */
    public List<String> levelNames;

    /**
     * A single value means same value for all the members + allMember override.
     */
    public List<String> levelUniqueNames;

    /**
     * A single value means same value for all the members + allMember override.
     */
    public List<Integer> levelDepths;

    /**
     * A single value means same value for all the members + allMember override.
     * <p>
     * Relative (starting at 0 + fixing ragged dimensions sending visual depths).
     */
    public List<Integer> levelDepthsR;

    @Nullable
    public List<Object> keys;

    /**
     * Keys same as names : use the names field instead.
     */
    public boolean keysN;

    /**
     * A single value means same value for all the members + allMember override.
     */
    @Nullable
    public List<String> parentUniqueNames;

    /**
     * A single value means same value for all the members + allMember override.
     */
    @Nullable
    public List<Boolean> alls;

    /**
     * A single value means same value for all the members + allMember override.
     */
    @Nullable
    public List<String> formatStrings;

    @Nullable
    public List<String> colors;

    @Nullable
    public List<String> ic3iso2;

    @Nullable
    public List<Number> ic3lat;

    @Nullable
    public List<Number> ic3long;

    /**
     * The list of requested properties (DIMENSION PROPERTIES).
     */
    @Nullable
    public List<String> dpNames;

    @Nullable
    public Map<String, AORestApiTidyTableEntityType> dpTypes;

    @Nullable
    public Map<String, ArrayList<Object>> dpValues;

    @Nullable
    private Map<String, AORestApiTidyTableColumnNaN> dpValuesN;

    public int getMemberIndex(int tupleIndex)
    {
        return memberInfoIndices.get(tupleIndex);
    }

    public String getName(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.name;
        }

        return names.get(memberIndex);
    }

    public String getCaption(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.caption;
        }

        if (captions == null)
        {
            return getName(memberIndex);
        }

        return captions.get(memberIndex);
    }

    public String getUniqueName(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.uniqueName;
        }

        final Object un = uniqueNames.get(uniqueNames.size() == 1 ? 0 : memberIndex);

        if (un instanceof String uns)
        {
            return uns;
        }

        if (un instanceof Integer uni)
        {
            final AORestApiMdxMemberUniqueNameType uniqueNameType = AORestApiMdxMemberUniqueNameType.fromTidyTableValue(uni);
            return generateUniqueName(uniqueNameType, memberIndex);
        }

        // Introduced w/ Infoland test runner : dunno why got some double over here.
        if (un instanceof Double und && und == Math.rint(und))
        {
            final AORestApiMdxMemberUniqueNameType uniqueNameType = AORestApiMdxMemberUniqueNameType.fromTidyTableValue(und.intValue());
            return generateUniqueName(uniqueNameType, memberIndex);
        }

        throw new RuntimeException("internal error : unexpected member unique type : " + un);
    }

    public int getChildrenCount(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.childrenCount;
        }

        return childrenCounts.get(childrenCounts.size() == 1 ? 0 : memberIndex);
    }

    public String getLevelCaption(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.level.levelCaption;
        }

        return levelCaptions.get(levelCaptions.size() == 1 ? 0 : memberIndex);
    }

    public String getLevelName(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.level.levelName;
        }

        return levelNames.get(levelNames.size() == 1 ? 0 : memberIndex);
    }

    public String getLevelUniqueName(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.level.levelUniqueName;
        }

        return levelUniqueNames.get(levelUniqueNames.size() == 1 ? 0 : memberIndex);
    }

    public int getLevelDepth(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.level.levelDepth;
        }

        return levelDepths.get(levelDepths.size() == 1 ? 0 : memberIndex);
    }

    public int getLevelDepthR(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.level.levelDepthR;
        }

        return levelDepthsR.get(levelDepthsR.size() == 1 ? 0 : memberIndex);
    }

    @Nullable
    public Object getKey(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.key;
        }

        return keys != null ? keys.get(memberIndex) : (keysN ? getName(memberIndex) : null);
    }

    @Nullable
    public String getParentUniqueName(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return null;
        }

        return parentUniqueNames != null ? parentUniqueNames.get(parentUniqueNames.size() == 1 ? 0 : memberIndex) : null;
    }

    public boolean isAll(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return true;
        }

        return alls != null && (alls.get(alls.size() == 1 ? 0 : memberIndex) == Boolean.TRUE);
    }

    @Nullable
    public String getFormatString(int memberIndex)
    {
        if (allMember != null && allMember.index == memberIndex)
        {
            return allMember.formatString;
        }

        return formatStrings != null ? formatStrings.get(formatStrings.size() == 1 ? 0 : memberIndex) : null;
    }

    @Nullable
    public String getColor(int memberIndex)
    {
        if (colors != null)
        {
            return colors.get(memberIndex);
        }
        return null;
    }

    @Nullable
    public String getIc3Iso2(int memberIndex)
    {
        if (ic3iso2 != null)
        {
            return ic3iso2.get(memberIndex);
        }
        return null;
    }

    @Nullable
    public Number getIc3Lat(int memberIndex)
    {
        if (ic3lat != null)
        {
            return ic3lat.get(memberIndex);
        }
        return null;
    }

    @Nullable
    public Number getIc3Long(int memberIndex)
    {
        if (ic3long != null)
        {
            return ic3long.get(memberIndex);
        }
        return null;
    }

    @Nullable
    public List<String> getDpNames()
    {
        return dpNames;
    }

    @Nullable
    public AORestApiTidyTableEntityType getDpType(String prop)
    {
        if (dpTypes == null)
        {
            return null;
        }
        return dpTypes.get(prop);
    }

    @Nullable
    public List<Object> getDpValues(String prop)
    {
        if (dpValues == null)
        {
            return null;
        }
        return dpValues.get(prop);
    }

    private String generateUniqueName(AORestApiMdxMemberUniqueNameType uniqueNameType, int memberIndex)
    {
        return switch (uniqueNameType)
        {
            case LEVEL_KEY -> generateUniqueNameLevelKey(memberIndex);
            case LEVEL_NAME -> generateUniqueNameLevelName(memberIndex);
            case PARENT_KEY -> generateUniqueNameParentKey(memberIndex);
            case PARENT_NAME -> generateUniqueNameParentName(memberIndex);

            default -> throw new RuntimeException("internal error : unexpected unique type [" + uniqueNameType + "]");
        };
    }

    private String generateUniqueNameLevelKey(int memberIndex)
    {
        final String levelUniqueName = getLevelUniqueName(memberIndex);
        final Object key = getKey(memberIndex);

        return levelUniqueName + ".&[" + AORestApiMdxUtils.escape(key.toString()) + "]";
    }

    private String generateUniqueNameLevelName(int memberIndex)
    {
        final String levelUniqueName = getLevelUniqueName(memberIndex);
        final String name = names.get(memberIndex);

        return levelUniqueName + ".[" + AORestApiMdxUtils.escape(name) + "]";
    }

    private String generateUniqueNameParentKey(int memberIndex)
    {
        final String parentUniqueName = getParentUniqueName(memberIndex);
        final Object key = getKey(memberIndex);

        return parentUniqueName + ".&[" + AORestApiMdxUtils.escape(key.toString()) + "]";
    }

    private String generateUniqueNameParentName(int memberIndex)
    {
        final String parentUniqueName = getParentUniqueName(memberIndex);
        final String name = names.get(memberIndex);

        return parentUniqueName + ".[" + AORestApiMdxUtils.escape(name) + "]";
    }
}
