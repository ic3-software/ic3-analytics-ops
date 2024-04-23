package ic3.analyticsops.stats;

public class AOIntegerStatsColumn extends AOStatsColumn<Integer>
{
    private int[] data = new int[16];

    private int pos;

    public AOIntegerStatsColumn(String name)
    {
        super(name);
    }

    public Integer getValue(int index)
    {
        return getIntValue(index);
    }

    public int getIntValue(int index)
    {
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
