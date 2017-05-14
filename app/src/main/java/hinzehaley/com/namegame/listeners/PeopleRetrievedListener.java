package hinzehaley.com.namegame.listeners;

import com.android.volley.VolleyError;

import models.profiles.Profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public interface PeopleRetrievedListener {

    void peopleRetrieved(Profiles profiles);

    void errorResponse(VolleyError error);
}
