package hinzehaley.com.namegame;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hinzehaley.com.namegame.adapters.RecyclerViewImageAdapter;
import hinzehaley.com.namegame.adapters.RecyclerViewNameAdapter;
import hinzehaley.com.namegame.dialogs.DialogManager;
import hinzehaley.com.namegame.listeners.DialogButtonClickListener;
import hinzehaley.com.namegame.listeners.PeopleRetrievedListener;
import hinzehaley.com.namegame.listeners.PersonClickedListener;
import models.VolleyPersonRequester;
import models.profiles.Items;
import models.profiles.Profiles;

public class NameGameActivity extends AppCompatActivity implements PeopleRetrievedListener, PersonClickedListener {

    RecyclerView recyclerViewFaces;
    VolleyPersonRequester volleyPersonRequester;
    static Profiles profiles;
    static HashMap<String, Items> activeProfiles = new HashMap<String, Items>();
    static Items[] curProfiles = new Items[Constants.NUM_FACES];
    RecyclerViewImageAdapter adapterFaces;
    RecyclerViewNameAdapter adapterNames;


    TextView tvScore;


    TextView tvName;
    ImageView imgPerson;
    ProgressBar progressBar;
    FrameLayout frameInfo;


    static int numCorrect = 0;
    static int numTotal = 0;
    static Items curProfile;

    public enum Mode {
        NORMAL, REVERSE, MATT, TEST
    }

    private static Mode mode;
    private static boolean showingImageQuestion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViewItems();

        setUpRecyclerview();
        mode = Mode.NORMAL;
    }

    private void setupViewItems() {
        tvScore = (TextView) findViewById(R.id.txt_score);
        tvName = (TextView) findViewById(R.id.txt_name);
        imgPerson = (ImageView) findViewById(R.id.img_person);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        frameInfo = (FrameLayout) findViewById(R.id.frame_info);


    }

    private void restartGame() {
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
    private void setUpRecyclerview() {
        if (recyclerViewFaces == null) {
            recyclerViewFaces = (RecyclerView) findViewById(R.id.recycler_faces);
        }

        if (isPortrait()) {
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            //start at leftmost position
            llm.setStackFromEnd(true);
            recyclerViewFaces.setLayoutManager(llm);
        } else {
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            //start at top
            llm.setStackFromEnd(true);
            recyclerViewFaces.setLayoutManager(llm);
        }

        adapterFaces = new RecyclerViewImageAdapter(new Items[0], this, this);
        adapterNames = new RecyclerViewNameAdapter(new Items[0], this, this);
        recyclerViewFaces.setAdapter(adapterFaces);


    }

    private boolean isPortrait(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }
        return false;
    }

    /**
     * Gets volleyPersonRequester if necessary. Checks for a network connection.
     * If connected, shows a progress dialog and requests profiles using volleyPersonRequester.
     * If not connected, shows a dialog to request that the user connects
     */
    private void requestProfiles() {
        if (volleyPersonRequester == null) {
            volleyPersonRequester = VolleyPersonRequester.getInstance();
        }
        if (networkConnected()) {
            DialogManager.showProgressDialog(this);
            volleyPersonRequester.requestPeople(this, this);
        } else {
            DialogManager.showNetworkNotConnectedDialog(this);
        }
    }

    /**
     * Callback method from volleyPersonRequester when profiles are retrieved
     * successfully
     *
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

    /**
     * initializes activeProfiles with all profiles we want to learn about. In Matt mode, add
     * only Matts and Matthews. All other modes, add everyone
     */
    private void initializeActiveProfiles() {
        activeProfiles.clear();
        for (int i = 0; i < profiles.getItems().length; i++) {
            switch(mode){
                case MATT:
                    String name = profiles.getItems()[i].getFirstName().toLowerCase();
                    if(name.equals(Constants.MATT) || name.equals(Constants.MATTHEW) || name.equals(Constants.MAT)){
                        activeProfiles.put(profiles.getItems()[i].getId(), profiles.getItems()[i]);
                    }else{
                        continue;
                    }
            }
            activeProfiles.put(profiles.getItems()[i].getId(), profiles.getItems()[i]);
        }
    }

    private void gameOver() {
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
    private void askQuestion() {
        //go to start of recyclerView
        recyclerViewFaces.scrollToPosition(adapterFaces.getItemCount() - 1);

        Log.i("HASHMAP", "size is: " + activeProfiles.size());

        if (activeProfiles.size() == 0) {
            gameOver();
            return;
        }

        List<String> keysAsArray = new ArrayList<String>(activeProfiles.keySet());
        Random random = new Random();
        int rand = random.nextInt(keysAsArray.size());
        curProfile = activeProfiles.get(keysAsArray.get(rand));

        int randIndex = random.nextInt(Constants.NUM_FACES);

        int showImage = 0;

        for (int i = 0; i < Constants.NUM_FACES; i++) {
            if (i == randIndex) {
                curProfiles[i] = curProfile;
            } else {
                switch(mode){
                    case TEST:
                        rand = random.nextInt(profiles.getItems().length);
                        curProfiles[i] = profiles.getItems()[rand];
                        showImage = random.nextInt(2);
                        break;
                    case REVERSE:
                        showImage = 1;
                    default:
                        rand = random.nextInt(keysAsArray.size());
                        curProfiles[i] = activeProfiles.get(keysAsArray.get(rand));
                        break;

                }

            }
        }
        if(showImage == 0) {
            recyclerViewFaces.setAdapter(adapterFaces);
            adapterFaces.updateProfiles(curProfiles);
            showName();
        }else{
            recyclerViewFaces.setAdapter(adapterNames);
            adapterNames.updateProfiles(curProfiles);
            showImage();
        }

    }

    private void showImage(){
        LinearLayout.LayoutParams paramsTall = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3);
        LinearLayout.LayoutParams paramsShort = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2);


        if(isPortrait()){
            frameInfo.setLayoutParams(paramsTall);
            recyclerViewFaces.setLayoutParams(paramsShort);
        }
        showingImageQuestion = true;
        progressBar.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.GONE);
        imgPerson.setVisibility(View.VISIBLE);
        String modUrl = "http:" + curProfile.getHeadshot().getUrl();

        Picasso.with(this).load(modUrl).error(R.drawable.ic_error).fit().centerCrop().into(imgPerson, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showName(){

        LinearLayout.LayoutParams paramsTall = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3);
        LinearLayout.LayoutParams paramsShort = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2);


        if(isPortrait()){
            frameInfo.setLayoutParams(paramsShort);
            recyclerViewFaces.setLayoutParams(paramsTall);
        }
        showingImageQuestion = false;
        tvName.setText(curProfile.getFirstName() + " " + curProfile.getLastName());
        tvName.setVisibility(View.VISIBLE);
        imgPerson.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

    }



    /**
     * Callback from volleyPersonRequester if unable to retrieve profiles
     *
     * @param error
     */
    @Override
    public void errorResponse(VolleyError error) {
        DialogManager.hideProgressDialog();
        DialogManager.showProfileErrorDialog(error.getMessage(), this);
    }


    /**
     * Checks for a network connection
     *
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
        if (profiles == null) {
            requestProfiles();
        }
    }


    private void correctChoice() {
        activeProfiles.remove(curProfile.getId());
        numCorrect++;
        numTotal++;
        askQuestion();
        showSnackbar(getString(R.string.correct));
        updateScore();
    }

    private void incorrectChoice() {
        numTotal++;
        showSnackbar(getString(R.string.incorrect));
        updateScore();
        switch(mode){
            case TEST:
                activeProfiles.remove(curProfile.getId());
                break;
        }
        askQuestion();
    }

    private void updateScore() {
        tvScore.setText(getString(R.string.score) + " " + numCorrect + "/" + numTotal);
    }


    @Override
    public void personClicked(Items person) {
        if (curProfile.getId() == person.getId()) {
            correctChoice();
        } else {
            incorrectChoice();
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .show();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.MODE, mode);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        updateView();
        mode = (Mode) savedInstanceState.get(Constants.MODE);
        super.onRestoreInstanceState(savedInstanceState);


    }

    private void updateView() {
        if(showingImageQuestion){
            showImage();
            adapterNames.updateProfiles(curProfiles);
            recyclerViewFaces.setAdapter(adapterNames);

        }else{
            showName();
            adapterFaces.updateProfiles(curProfiles);
            recyclerViewFaces.setAdapter(adapterFaces);
        }
        updateScore();
    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgressDialog();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_name_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_learn:
                mode = Mode.NORMAL;
                restartGame();
                return true;
            case R.id.action_matt:
                mode = Mode.MATT;
                restartGame();
                return true;
            case R.id.action_reverse:
                mode = Mode.REVERSE;
                restartGame();
                return true;
            case R.id.action_test:
                mode = Mode.TEST;
                restartGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
