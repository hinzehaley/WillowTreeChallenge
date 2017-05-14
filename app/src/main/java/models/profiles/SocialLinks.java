package models.profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class SocialLinks
{
    private String callToAction;

    private String type;

    private String url;

    public String getCallToAction ()
    {
        return callToAction;
    }

    public void setCallToAction (String callToAction)
    {
        this.callToAction = callToAction;
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

    @Override
    public String toString()
    {
        return "ClassSocialLinks [callToAction = "+callToAction+", type = "+type+", url = "+url+"]";
    }
}
