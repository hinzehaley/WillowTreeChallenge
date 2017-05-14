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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import hinzehaley.com.namegame.adapters.RecyclerViewImageAdapter;
import hinzehaley.com.namegame.dialogs.DialogManager;
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

    TextView tvScore;
    TextView tvName;
    Button btnReset;

    int numCorrect = 0;
    int numTotal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViewItems();

        setUpRecyclerview();
    }

    private void setupViewItems(){
        tvScore = (TextView) findViewById(R.id.txt_score);
        tvName = (TextView) findViewById(R.id.txt_name);
        btnReset = (Button) findViewById(R.id.btn_restart);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogManager.showAreYouSureYouWantToRestartDialog(NameGameActivity.this);
            }
        });
    }



    public void restartGame(){
        numCorrect = 0;
        numTotal = 0;
        askQuestion();
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
            //start at leftmost position
            llm.setStackFromEnd(true);
            recyclerViewFaces.setLayoutManager(llm);
        }else{
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            //start at top
            llm.setStackFromEnd(true);
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
            DialogManager.showProgressDialog(this);
            volleyPersonRequester.requestPeople(this, this);
        }else{
            DialogManager.showNetworkNotConnectedDialog(this);
        }
    }

    /**
     * Callback method from volleyPersonRequester when profiles are retrieved
     * successfully
     * @param profiles
     */
    @Override
    public void peopleRetrieved(Profiles profiles) {
        DialogManager.hideProgressDialog();
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
        DialogManager.hideProgressDialog();
        DialogManager.showProfileErrorDialog(error.getMessage(), this);
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


}
