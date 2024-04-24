package ic3.analyticsops.stats.column;

import ic3.analyticsops.stats.AOStatsColumn;
import org.jetbrains.annotations.Nullable;

public class AOLongStatsColumn extends AOStatsColumn<Long>
{
    private long[] data = new long[16];

    private int pos;

    public AOLongStatsColumn(String name)
    {
        super(name);
    }

    @Nullable
    public Long getValue(int index)
    {
        if (index < 0 || index >= pos)
        {
            return null;
        }

        return getLongValue(index);
    }

    public long getLongValue(int index)
    {
        if (index < 0 || index >= pos)
        {
            return 0;
        }

        return data[index];
    }

    public void setLongValue(int index, long value)
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
            final long[] tmp = new long[newCapacity];

            System.arraycopy(data, 0, tmp, 0, data.length);

            data = tmp;
        }
    }

}
