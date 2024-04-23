package ic3.analyticsops.stats;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AOMinLongStatsColumn extends AOStatsColumn<Long>
{
    private long[] data = new long[16];

    private int pos;

    public AOMinLongStatsColumn(String name)
    {
        super(name);

        Arrays.fill(data, Long.MAX_VALUE);
    }

    @Nullable
    public Long getValue(int index)
    {
        final long val = getLongValue(index);
        return val == Long.MAX_VALUE ? null : val;
    }

    public long getLongValue(int index)
    {
        return data[index];
    }

    public void setLongValue(int index, long value)
    {
        if (index >= pos)
        {
            ensureCapacity(index + 1);
            pos = index + 1;
        }

        data[index] = Math.min(data[index], value);
    }

    private void ensureCapacity(int capacity)
    {
        if (capacity > data.length)
        {
            final int newCapacity = Math.max((data.length * 3) / 2 + 1, capacity);
            final long[] tmp = new long[newCapacity];

            System.arraycopy(data, 0, tmp, 0, data.length);
            Arrays.fill(tmp, data.length, tmp.length, Long.MAX_VALUE);

            data = tmp;
        }
    }

}
