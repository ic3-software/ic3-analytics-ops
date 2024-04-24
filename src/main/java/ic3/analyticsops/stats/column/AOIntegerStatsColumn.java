package ic3.analyticsops.stats.column;

import ic3.analyticsops.stats.AOStatsColumn;
import org.jetbrains.annotations.Nullable;

public class AOIntegerStatsColumn extends AOStatsColumn<Integer>
{
    private int[] data = new int[16];

    private int pos;

    public AOIntegerStatsColumn(String name)
    {
        super(name);
    }

    @Nullable
    public Integer getValue(int index)
    {
        if (index < 0 || index >= pos)
        {
            return null;
        }

        return getIntValue(index);
    }

    public int getIntValue(int index)
    {
        if (index < 0 || index >= pos)
        {
            return 0;
        }

        return data[index];
    }

    public void setIntValue(int index, int value)
    {
        if (index >= pos)
        {
            ensureCapacity(index + 1);
            pos = index + 1;
        }

        data[index] = value;
    }

    private void ensureCapacity(int capacity)
    {
        if (capacity > data.length)
        {
            final int newCapacity = Math.max((data.length * 3) / 2 + 1, capacity);
            final int[] tmp = new int[newCapacity];

            System.arraycopy(data, 0, tmp, 0, data.length);

            data = tmp;
        }
    }

}
