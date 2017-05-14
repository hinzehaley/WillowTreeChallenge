package models.profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class Meta
{
    private String total;

    private String limit;

    private String skip;

    public String getTotal ()
    {
        return total;
    }

    public void setTotal (String total)
    {
        this.total = total;
    }

    public String getLimit ()
    {
        return limit;
    }

    public void setLimit (String limit)
    {
        this.limit = limit;
    }

    public String getSkip ()
    {
        return skip;
    }

    public void setSkip (String skip)
    {
        this.skip = skip;
    }

    @Override
    public String toString()
    {
        return "ClassMeta [total = "+total+", limit = "+limit+", skip = "+skip+"]";
    }
}

