package ic3.analyticsops.stats;

import org.jetbrains.annotations.Nullable;

public abstract class AOStatsColumn<VALUE>
{
    private final String name;

    public AOStatsColumn(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Nullable
    public abstract VALUE getValue(int rowNb);

    @Override
    public String toString()
    {
        return name;
    }
}
