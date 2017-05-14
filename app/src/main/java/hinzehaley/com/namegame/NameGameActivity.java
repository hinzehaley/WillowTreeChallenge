package hinzehaley.com.namegame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.VolleyError;

import hinzehaley.com.namegame.adapters.RecyclerViewImageAdapter;
import hinzehaley.com.namegame.listeners.PeopleRetrievedListener;
import models.VolleyPersonRequester;
import models.profiles.Items;
import models.profiles.Profiles;

public class NameGameActivity extends AppCompatActivity implements PeopleRetrievedListener{

    RecyclerView recyclerViewFaces;
    VolleyPersonRequester volleyPersonRequester;
    Profiles profiles;
    Items[] curProfiles = new Items[Constants.NUM_FACES];
    RecyclerViewImageAdapter adapterFaces;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerview();
    }

    /**
     * Initializes adapterFaces and Sets up recyclerView with no faces initially
     * With a horizontal scroll if in portraid mode, vertical scroll in landscape mode
     */
    private void setUpRecyclerview(){
        if(recyclerViewFaces == null) {
            recyclerViewFaces = (RecyclerView) findViewById(R.id.recycler_faces);
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            recyclerViewFaces.setLayoutManager(llm);
        }else{
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            recyclerViewFaces.setLayoutManager(llm);
        }

        adapterFaces = new RecyclerViewImageAdapter(new Items[0], this);
        recyclerViewFaces.setAdapter(adapterFaces);

    }

    /**
     * Gets volleyPersonRequester if necessary. Checks for a network connection.
     * If connected, shows a progress dialog and requests profiles using volleyPersonRequester.
     * If not connected, shows a dialog to request that the user connects
     */
    private void requestProfiles(){
        if(volleyPersonRequester == null){
            volleyPersonRequester = VolleyPersonRequester.getInstance();
        }
        if(networkConnected()) {
            showProgressDialog();
            volleyPersonRequester.requestPeople(this, this);
        }else{
            showNetworkNotConnectedDialog();
        }
    }

    /**
     * Callback method from volleyPersonRequester when profiles are retrieved
     * successfully
     * @param profiles
     */
    @Override
    public void peopleRetrieved(Profiles profiles) {
        hideProgressDialog();
        Log.i("VOLLEY", "got profiles successfully! " + profiles.getItems().length);
        this.profiles = profiles;
        askQuestion();

    }

    /**
     * Asks a new question by presenting a name and six faces
     */
    private void askQuestion(){

        Items[] items = profiles.getItems();
        for(int i = 0; i<Constants.NUM_FACES; i++){
           curProfiles[i] = items[i];
        }
        adapterFaces.updateProfiles(curProfiles);

        //TODO: fill in with actual quiz
    }

    /**
     * Callback from volleyPersonRequester if unable to retrieve profiles
     * @param error
     */
    @Override
    public void errorResponse(VolleyError error) {
        hideProgressDialog();
        showProfileErrorDialog(error.getMessage());
    }


    /**
     * Shows a dialog telling the user to connect to a network
     */
    private void showNetworkNotConnectedDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.network));
        alertDialog.setMessage(getString(R.string.not_connected));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Shows a dialog telling the user there was an error retrieving the profiles
     * @param errorMessage
     */
    private void showProfileErrorDialog(String errorMessage){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage(getString(R.string.error_getting_profiles) + errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Checks for a network connection
     * @return true if connected, otherwise false
     */
    private boolean networkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Requests profiles. This is done in onResume so that we try again if user leaves
     * to connect to a network and comes back
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(profiles == null) {
            requestProfiles();
        }
    }

    /**
     * Shows a progress dialog to display while retrieving profiles
     */
    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setMessage(getString(R.string.get_profiles));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    /**
     * Hides progress dialog
     */
    private void hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}
