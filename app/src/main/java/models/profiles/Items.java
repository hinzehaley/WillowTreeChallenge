package models.profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class Items
{
    private String id;

    private String lastName;

    private Headshot headshot;

    private String[] socialLinks;

    private String slug;

    private String firstName;

    private String type;

    private String jobTitle;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getLastName ()
    {
        return lastName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }

    public Headshot getHeadshot ()
    {
        return headshot;
    }

    public void setHeadshot (Headshot headshot)
    {
        this.headshot = headshot;
    }

    public String[] getSocialLinks ()
    {
        return socialLinks;
    }

    public void setSocialLinks (String[] socialLinks)
    {
        this.socialLinks = socialLinks;
    }

    public String getSlug ()
    {
        return slug;
    }

    public void setSlug (String slug)
    {
        this.slug = slug;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getJobTitle ()
    {
        return jobTitle;
    }

    public void setJobTitle (String jobTitle)
    {
        this.jobTitle = jobTitle;
    }

    @Override
    public String toString()
    {
        return "ClassItems [id = "+id+", lastName = "+lastName+", headshot = "+headshot+", socialLinks = "+socialLinks+", slug = "+slug+", firstName = "+firstName+", type = "+type+", jobTitle = "+jobTitle+"]";
    }
}
