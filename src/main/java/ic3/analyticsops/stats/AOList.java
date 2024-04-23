package ic3.analyticsops.stats;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AOList<VALUE>
{
    private Object[] data = new Object[16];

    private int pos;

    public AOList()
    {
    }

    public <V extends VALUE> V computeIfAbsent(int index, Supplier<V> supplier)
    {
        VALUE column = get(index);

        if (column == null)
        {
            column = set(index, supplier.get());
        }

        return (V) column;
    }

    @Nullable
    public VALUE get(int index)
    {
        return (VALUE) data[index];
    }

    public VALUE set(int index, VALUE value)
    {
        if (index >= pos)
        {
            ensureCapacity(index + 1);
            pos = index + 1;
        }

        data[index] = value;

        return value;
    }

    public int size()
    {
        return pos;
    }

    private void ensureCapacity(int capacity)
    {
        if (capacity > data.length)
        {
            final int newCapacity = Math.max((data.length * 3) / 2 + 1, capacity);
            final Object[] tmp = new Object[newCapacity];

            System.arraycopy(data, 0, tmp, 0, data.length);

            data = tmp;
        }
    }


}
