package ic3.analyticsops.restapi.reply.tidy.mdx;

import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTableColumn;
import ic3.analyticsops.test.AOAssertion;
import org.jetbrains.annotations.Nullable;

public class AORestApiMdxTidyTableMemberColumn extends AORestApiMdxTidyTableColumn
{
    public String getName(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getName);
    }

    public String getUniqueName(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getUniqueName);
    }

    public String getCaption(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getCaption);
    }

    public int getChildrenCount(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getChildrenCount);
    }

    public String getLevelCaption(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getLevelCaption);
    }

    public String getLevelName(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getLevelName);
    }

    public String getLevelUniqueName(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getLevelUniqueName);
    }

    public int getLevelDepth(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getLevelDepth);
    }

    public int getLevelDepthR(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getLevelDepthR);
    }

    @Nullable
    public Object getKey(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getKey);
    }

    @Nullable
    public String getParentUniqueName(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getParentUniqueName);
    }

    public boolean isAll(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::isAll);
    }

    @Nullable
    public String getFormatString(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getFormatString);
    }

    @Nullable
    public String getColor(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getColor);
    }

    @Nullable
    public String getIc3Iso2(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getIc3Iso2);
    }

    @Nullable
    public Number getIc3Lat(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getIc3Lat);
    }

    @Nullable
    public Number getIc3Long(int rowIndex)
    {
        return getMemberInfo(rowIndex, AORestApiMdxTidyTableMembersInfo::getIc3Long);
    }

    public <INFO> INFO getMemberInfo(int rowIndex, AORestApiMdxTidyTableMemberInfoSupplier<INFO> supplier)
    {
        final AORestApiMdxTidyTable table = getTable();

        final AORestApiMdxTidyTableAxis axisInfo = table.getAxis(axis.axis);
        final AORestApiMdxTidyTableMembersInfo membersInfo = table.getMembers(axis.axis, axis.hierarchyIdx);

        final int tupleIndex = axisInfo.getTupleIndex(rowIndex);
        final int memberIndex = membersInfo.getMemberIndex(tupleIndex);

        return supplier.get(membersInfo, memberIndex);
    }

    @Override
    public int prettyPrintMaxWidth()
    {
        final AORestApiMdxTidyTable table = getTable();

        int max = 0;

        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.NAME).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.TYPE).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.SUB_TYPE).length());
        max = Math.max(max, prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader.FORMAT_STRING).length());

        for (int rr = 0; rr < table.rowCount; rr++)
        {
            max = Math.max(max, prettyPrint(rr).length());
        }

        return max;
    }

    @Override
    public String prettyPrintHeader(AORestApiMdxTidyTablePrettyPrinterHeader header)
    {
        final AORestApiMdxTidyTableMemberType kt = axis.getType();
        final AORestApiMdxTidyTableMemberSubType lt = axis.getSubType();

        return switch (header)
        {
            case NAME -> name + "[" + caption + "]";
            case TYPE -> "KT: " + (kt != null ? kt : "");
            case SUB_TYPE -> "LT: " + (lt != null ? lt : "");
            case FORMAT_STRING -> "FS: ";
            case SEPARATOR -> "--";
        };
    }

    @Override
    public String prettyPrint(int rowIndex)
    {
        return getName(rowIndex);
    }

    @Override
    public void assertEquals(AORestApiTidyTableColumn other, boolean valueOnly, double delta)
    {
        super.assertEquals(other, valueOnly, delta);

        final AORestApiMdxTidyTableMemberColumn actual = (AORestApiMdxTidyTableMemberColumn) other;

        final AORestApiMdxTidyTable table = getTable();

        final int rowCount = table.rowCount;

        for (int rr = 0; rr < rowCount; rr++)
        {
            final String caption = getCaption(rr);
            final String captionActual = actual.getCaption(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-caption", caption, captionActual);

            final String name = getName(rr);
            final String nameActual = actual.getName(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-name", name, nameActual);

            final String uniqueName = getUniqueName(rr);
            final String uniqueNameActual = actual.getUniqueName(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-unique-name", uniqueName, uniqueNameActual);

            final int childrenCount = getChildrenCount(rr);
            final int childrenCountActual = actual.getChildrenCount(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-children-count", childrenCount, childrenCountActual);

            // Level

            final String levelCaption = getLevelCaption(rr);
            final String levelCaptionActual = actual.getLevelCaption(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] level-caption", levelCaption, levelCaptionActual);

            final String levelName = getLevelName(rr);
            final String levelNameActual = actual.getLevelName(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] level-name", levelName, levelNameActual);

            final String levelUniqueName = getLevelUniqueName(rr);
            final String levelUniqueNameActual = actual.getLevelUniqueName(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] level-unique-name", levelUniqueName, levelUniqueNameActual);

            final int levelDepth = getLevelDepth(rr);
            final int levelDepthActual = actual.getLevelDepth(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] level-depth", levelDepth, levelDepthActual);

            final int levelDepthR = getLevelDepthR(rr);
            final int levelDepthRActual = actual.getLevelDepthR(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] level-depth-relative", levelDepthR, levelDepthRActual);

            // Key / Parent Unique Name

            final Object key = getKey(rr);
            final Object keyActual = actual.getKey(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-key", key, keyActual);

            final String parentUniqueName = getParentUniqueName(rr);
            final String parentUniqueNameActual = actual.getParentUniqueName(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-parent-unique-name", parentUniqueName, parentUniqueNameActual);

            // Other

            final Object isAll = isAll(rr);
            final Object isAllActual = actual.isAll(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-all", isAll, isAllActual);

            final String formatString = getFormatString(rr);
            final String formatStringActual = actual.getFormatString(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-format-string", formatString, formatStringActual);

            final String color = getColor(rr);
            final String colorActual = actual.getColor(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-color", color, colorActual);

            final String iso2 = getIc3Iso2(rr);
            final String iso2Actual = actual.getIc3Iso2(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-iso2", iso2, iso2Actual);

            final Number latitude = getIc3Lat(rr);
            final Number latitudeActual = actual.getIc3Lat(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-latitude", latitude, latitudeActual);

            final Number longitude = getIc3Long(rr);
            final Number longitudeActual = actual.getIc3Long(rr);

            AOAssertion.assertEquals("column[" + name + "] row[" + rr + "] member-longitude", longitude, longitudeActual);
        }
    }
}
