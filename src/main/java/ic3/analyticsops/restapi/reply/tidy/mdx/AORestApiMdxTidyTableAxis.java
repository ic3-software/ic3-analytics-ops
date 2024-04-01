package ic3.analyticsops.restapi.reply.tidy.mdx;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AORestApiMdxTidyTableAxis
{
    /**
     * The MDX axis has been converted to tidy table columns.
     */
    public boolean asColumns;

    public int axis;

    @Nullable
    public String role;

    public int hierarchyCount;

    public int length;

    public boolean hasNonEmpty;

    /**
     * The position of the tuple in the axis for each row. A tuple is possibly repeated because of other MDX axes
     * creating a crossjoin.
     * <p>
     * Each MDX axis is converted into one or more members columns (one for each hierarchy of the tuples).
     * Each of these columns is referencing that same content.
     * <p>
     * The client code is calling that one: rowIndexToTupleIndex.
     * <p>
     * When the axis0 is converted to columns, this content contains [0,1,2,3,... length-1] and the tuple index
     * does not correspond to any row (each tuple being converted in a cell column).
     */
    public List<Integer> content;

    public int getTupleIndex(int rowIndex)
    {
        return content.get(rowIndex);
    }

}
