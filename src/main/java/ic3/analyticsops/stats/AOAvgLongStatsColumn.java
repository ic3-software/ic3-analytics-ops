package ic3.analyticsops.stats;

import org.jetbrains.annotations.Nullable;

public class AOAvgLongStatsColumn extends AOStatsColumn<Long>
{
    private long[] sum = new long[16];

    private int[] count = new int[16];

    private int pos;

    public AOAvgLongStatsColumn(String name)
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

        final int c = count[index];
        return c == 0 ? null : getLongValue(index);
    }

    public long getLongValue(int index)
    {
        if (index < 0 || index >= pos)
        {
            return 0;
        }

        final int c = count[index];
        return c == 0 ? 0 : sum[index] / count[index];
    }

    public void setLongValue(int index, int weight, long value)
    {
        if (index >= pos)
        {
            ensureCapacity(index + 1);
            pos = index + 1;
        }

        count[index] += weight;
        sum[index] += weight * value;
    }

    private void ensureCapacity(int capacity)
    {
        if (capacity > sum.length)
        {
            final int newCapacity = Math.max((sum.length * 3) / 2 + 1, capacity);

            // data
            {
                final long[] tmpSum = new long[newCapacity];

                System.arraycopy(sum, 0, tmpSum, 0, sum.length);

                sum = tmpSum;
            }

            // count
            {
                final int[] tmpCount = new int[newCapacity];

                System.arraycopy(count, 0, tmpCount, 0, count.length);

                count = tmpCount;
            }
        }
    }

}
