package ic3.analyticsops.stats;

import ic3.analyticsops.test.AOActor;

public class AOStatsColumn
{
    private final AOActor actor;

    private final String name;

    private int[] data = new int[256];

    private int pos;

    public AOStatsColumn(AOActor actor, String name)
    {
        this.actor = actor;
        this.name = name;
    }

    public void addRow(int rowNb, int value)
    {
        if (rowNb >= pos)
        {
            ensureCapacity(rowNb + 1);
            pos = rowNb + 1;
            data[rowNb] = value;
        }
        else
        {
            if (rowNb >= 0)
            {
                data[rowNb] = value;
            }
            else
            {
                throw new ArrayIndexOutOfBoundsException(rowNb);
            }
        }
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

    public int getValue(int rowNb)
    {
        return data[rowNb];
    }
}
