package models;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;


import hinzehaley.com.namegame.listeners.PeopleRetrievedListener;
import models.profiles.Profiles;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class VolleyPersonRequester {


    static RequestQueue queue = null;

    private static VolleyPersonRequester requester;

    public static VolleyPersonRequester getInstance(){
        if(requester == null){
            requester = new VolleyPersonRequester();
        }
        return requester;
    }

    private VolleyPersonRequester(){

    }

    /**
     * Requests the Json data for the people
     * @param context
     * @param listener
     */

    public void requestPeople(final Context context, final PeopleRetrievedListener listener) {

        String urlPath = "https://www.willowtreeapps.com/api/v1.0/profiles";
        // Instantiate the RequestQueue.
        if(queue == null) {
            queue = Volley.newRequestQueue(context);
        }


        // Request a string response from the provided URL. Passes reponse into callerFragment
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, urlPath, null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String jsonString = response.toString();
                    Log.i("VOLLEY", "jsonString: " + jsonString);
                    Profiles profiles = new Gson().fromJson(jsonString, Profiles.class);
                    listener.peopleRetrieved(profiles);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", "error: " + error.getMessage());
                    error.printStackTrace();
                    listener.errorResponse(error);
                }
            });

        queue.add(stringRequest);
    }
}
