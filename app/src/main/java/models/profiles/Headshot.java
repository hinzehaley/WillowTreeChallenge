package models.profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class Headshot
{
    private String id;

    private String height;

    private String alt;

    private String width;

    private String type;

    private String url;

    private String mimeType;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getAlt ()
    {
        return alt;
    }

    public void setAlt (String alt)
    {
        this.alt = alt;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    public String getMimeType ()
    {
        return mimeType;
    }

    public void setMimeType (String mimeType)
    {
        this.mimeType = mimeType;
    }

    @Override
    public String toString()
    {
        return "ClassHeadshot [id = "+id+", height = "+height+", alt = "+alt+", width = "+width+", type = "+type+", url = "+url+", mimeType = "+mimeType+"]";
    }
}
