package models.profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class Profiles {

    private Items[] items;

    private Meta meta;

    public Items[] getItems ()
    {
        return items;
    }

    public void setItems (Items[] items)
    {
        this.items = items;
    }

    public Meta getMeta ()
    {
        return meta;
    }

    public void setMeta (Meta meta)
    {
        this.meta = meta;
    }

    @Override
    public String toString()
    {
        return "ClassProfiles [items = "+items+", meta = "+meta+"]";
    }

}
