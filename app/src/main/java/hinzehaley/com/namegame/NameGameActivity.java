package hinzehaley.com.namegame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hinzehaley.com.namegame.adapters.RecyclerViewImageAdapter;
import hinzehaley.com.namegame.dialogs.DialogManager;
import hinzehaley.com.namegame.listeners.DialogButtonClickListener;
import hinzehaley.com.namegame.listeners.PeopleRetrievedListener;
import hinzehaley.com.namegame.listeners.PersonClickedListener;
import models.VolleyPersonRequester;
import models.profiles.Items;
import models.profiles.Profiles;

public class NameGameActivity extends AppCompatActivity implements PeopleRetrievedListener, PersonClickedListener{

    RecyclerView recyclerViewFaces;
    VolleyPersonRequester volleyPersonRequester;
    Profiles profiles;
    HashMap<String, Items> activeProfiles = new HashMap<String, Items>();
    Items[] curProfiles = new Items[Constants.NUM_FACES];
    RecyclerViewImageAdapter adapterFaces;
    ProgressDialog mProgressDialog;

    TextView tvScore;
    TextView tvName;
    Button btnReset;

    int numCorrect = 0;
    int numTotal = 0;
    Items curProfile;


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
                DialogManager.showAreYouSureYouWantToRestartDialog(NameGameActivity.this, new DialogButtonClickListener() {
                    @Override
                    public void buttonClicked() {
                        restartGame();
                    }
                });
            }
        });
    }

    private void restartGame(){
        numCorrect = 0;
        numTotal = 0;
        initializeActiveProfiles();
        updateScore();
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

        adapterFaces = new RecyclerViewImageAdapter(new Items[0], this, this);
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
        initializeActiveProfiles();
        askQuestion();
    }

    private void initializeActiveProfiles(){
        for(int i = 0; i<profiles.getItems().length; i++){
            activeProfiles.put(profiles.getItems()[i].getId(), profiles.getItems()[i]);
        }
    }

    private void gameOver(){
        DialogManager.showGameOverDialog(getString(R.string.score) + numCorrect + "/" + numTotal, this, new DialogButtonClickListener() {
            @Override
            public void buttonClicked() {
                restartGame();
            }
        });
    }

    /**
     * Asks a new question by presenting a name and six faces
     */
    private void askQuestion(){

        if(activeProfiles.size() == 0){
            gameOver();
        }

        List<String> keysAsArray = new ArrayList<String>(activeProfiles.keySet());
        Random random = new Random();
        int rand = random.nextInt(keysAsArray.size());
        curProfile = activeProfiles.get(keysAsArray.get(rand));

        int randIndex = random.nextInt(Constants.NUM_FACES);

        for(int i = 0; i<Constants.NUM_FACES; i++){
            if(i == randIndex){
                curProfiles[i] = curProfile;
            }else{
                rand = random.nextInt(keysAsArray.size());
                curProfiles[i] = activeProfiles.get(keysAsArray.get(rand));
            }
        }
        adapterFaces.updateProfiles(curProfiles);
        tvName.setText(curProfile.getFirstName() + " " + curProfile.getLastName());
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

    private void correctChoice(){
        activeProfiles.remove(curProfile.getId());
        numCorrect++;
        numTotal++;
        askQuestion();
        showSnackbar(getString(R.string.correct));
        updateScore();
    }

    private void incorrectChoice(){
        numTotal++;
        askQuestion();
        showSnackbar(getString(R.string.incorrect));
        updateScore();
    }

    private void updateScore(){
        tvScore.setText(getString(R.string.score) + " " + numCorrect + "/" + numTotal);
    }


    @Override
    public void personClicked(Items person) {
        if(curProfile.getId() == person.getId()){
            correctChoice();
        }else{
            incorrectChoice();
        }
    }

    private void showSnackbar(String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
