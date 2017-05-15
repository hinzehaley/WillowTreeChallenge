package hinzehaley.com.namegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.android.volley.VolleyError;

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
import hinzehaley.com.namegame.modal.CorrectAnswerDialog;
import models.PicassoLoader;
import models.VolleyPersonRequester;
import models.profiles.Items;
import models.profiles.Profiles;

public class NameGameActivity extends AppCompatActivity implements PeopleRetrievedListener, PersonClickedListener {

    VolleyPersonRequester volleyPersonRequester;

    static Profiles profiles;
    static HashMap<String, Items> activeProfiles = new HashMap<String, Items>();
    static Items[] curProfiles = new Items[Constants.NUM_FACES];
    static Items curProfile;

    RecyclerViewImageAdapter adapterFaces;
    RecyclerViewNameAdapter adapterNames;
    RecyclerView recyclerViewFaces;

    TextView tvScore;
    FloatingActionButton btnRefresh;

    TextView tvName;
    ImageView imgPerson;
    ProgressBar progressBar;
    FrameLayout frameInfo;

    static int numCorrect = 0;
    static int numTotal = 0;

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

    /**
     * Gets view references and listens for refresh button clicks
     */
    private void setupViewItems() {
        tvScore = (TextView) findViewById(R.id.txt_score);
        tvName = (TextView) findViewById(R.id.txt_name);
        imgPerson = (ImageView) findViewById(R.id.img_person);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        frameInfo = (FrameLayout) findViewById(R.id.frame_info);
        btnRefresh = (FloatingActionButton) findViewById(R.id.fab_restart);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Sets score to zero, initializes active profiles, and asks a new question
     */
    private void restartGame() {
        numCorrect = 0;
        numTotal = 0;
        initializeActiveProfiles();
        updateScore();
        askQuestion();
    }

    /**
     * Initializes adapterFaces and Sets up recyclerView with no faces initially
     * With a horizontal scroll if in portrait mode, vertical scroll in landscape mode
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

    /**
     * Checks layout direction
     * @return true if portrait, false otherwise
     */
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

    /**
     * Called when game is over. Displays end message to user and gives option to restart.
     * Saves highscore if user gets new highscore in test mode.
     */
    private void gameOver() {
        String message = getString(R.string.score) + numCorrect + "/" + numTotal;
        switch (mode){
            case TEST:
                if(isHighscore()){
                    saveHighscore();
                    message = getString(R.string.new_highscore) + "\n" + message;
                }
                break;
        }
        DialogManager.showGameOverDialog(message, this, new DialogButtonClickListener() {
            @Override
            public void buttonClicked() {
                restartGame();
            }
        });
    }

    /**
     * Checks if game is over. If so, calls method to handle it
     * @return true if game over, false otherwise
     */
    private boolean handleGameOver(){
        if (activeProfiles.size() == 0) {
            gameOver();
            return true;
        }
        return false;
    }

    /**
     * Asks a new question by presenting a name and six faces
     */
    private void askQuestion() {

        if(handleGameOver()){
            return;
        }
        //go to start of recyclerView
        recyclerViewFaces.scrollToPosition(adapterFaces.getItemCount() - 1);

        //Gets random profile to quiz on
        List<String> keysAsArray = new ArrayList<String>(activeProfiles.keySet());
        Random random = new Random();
        int rand = random.nextInt(keysAsArray.size());
        curProfile = activeProfiles.get(keysAsArray.get(rand));

        //Gets random index to display correct profile at
        int randIndex = random.nextInt(Constants.NUM_FACES);

        //Indicates to show name and ask for face
        int showImage = 0;

        //Gets 5 random faces and adds them to curProfiles. Adds in the correct face at randIndex
        for (int i = 0; i < Constants.NUM_FACES; i++) {
            if (i == randIndex) {
                curProfiles[i] = curProfile;
            } else {
                switch(mode){
                    //In test mode, gets random int to decide if we should show name and ask face, or show
                    //face and ask name
                    case TEST:
                        //In test mode, images/names can come from all profiles
                        rand = random.nextInt(profiles.getItems().length);
                        curProfiles[i] = profiles.getItems()[rand];
                        showImage = random.nextInt(2);
                        break;
                    //In reverse mode, sets showImage to 1 to indicate that we are showing an image and
                    //asking for a name
                    case REVERSE:
                        showImage = 1;
                    default:
                        //In default mode, images/names come from only activeProfiles
                        rand = random.nextInt(keysAsArray.size());
                        curProfiles[i] = activeProfiles.get(keysAsArray.get(rand));
                        break;
                }
            }
        }

        //shows name and updates recyclerview to show faces
        if(showImage == 0) {
            recyclerViewFaces.setAdapter(adapterFaces);
            adapterFaces.updateProfiles(curProfiles);
            showName();
         //shows image and updates recyclerview to show names
        }else{
            recyclerViewFaces.setAdapter(adapterNames);
            adapterNames.updateProfiles(curProfiles);
            showImage();
        }
    }

    /**
     * Displays an image question.
     */
    private void showImage(){
        showingImageQuestion = true;
        updateLayoutRatios();
        progressBar.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.GONE);
        imgPerson.setVisibility(View.VISIBLE);

        if(curProfile != null) {
            PicassoLoader.loadInImage(this, imgPerson, progressBar, curProfile.getHeadshot().getUrl());
        }
    }

    /**
     * Sets layout to display the user's name instead of a picture. Updates view
     * ratios because the name takes less space.
     */
    private void showName(){
        showingImageQuestion = false;
        updateLayoutRatios();
        if(curProfile != null) {
            tvName.setText(curProfile.getFirstName() + " " + curProfile.getLastName());
            tvName.setVisibility(View.VISIBLE);
            imgPerson.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the layout ratios so that if an image question is showing, it has more space. If a
     * name question is showing, it has less space
     */
    private void updateLayoutRatios(){
        LinearLayout.LayoutParams paramsTall = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3);
        LinearLayout.LayoutParams paramsShort = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2);
        if(!showingImageQuestion && isPortrait()){
            frameInfo.setLayoutParams(paramsShort);
            recyclerViewFaces.setLayoutParams(paramsTall);
        }else if (showingImageQuestion && isPortrait()){
            frameInfo.setLayoutParams(paramsTall);
            recyclerViewFaces.setLayoutParams(paramsShort);
        }
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
        if (profiles == null) {
            requestProfiles();
        }
    }

    /**
     * Updates the score and shows it on UI. Removes the profile so the person
     * isn't asked about again Asks a new question.
     */
    private void correctChoice() {
        activeProfiles.remove(curProfile.getId());
        numCorrect++;
        numTotal++;
        askQuestion();
        showSnackbar(getString(R.string.correct));
        updateScore();
    }

    /**
     * Shows a message saying the choice was incorrect.
     * Updates the UI to show the new score. If in test mode, removes the person from
     * the active profiles so they aren't asked about again. If not in test mode,
     * calls a function to show a modal view with the correct answer. Asks a new question.
     */
    private void incorrectChoice() {
        numTotal++;
        showSnackbar(getString(R.string.incorrect));
        updateScore();
        switch(mode){
            case TEST:
                activeProfiles.remove(curProfile.getId());
                break;
            default:
                showCorrectAnswerDialog();
        }
        askQuestion();
    }

    /**
     * displays the score on the screen
     */
    private void updateScore() {
        tvScore.setText(getString(R.string.score) + " " + numCorrect + "/" + numTotal);
    }

    /**
     * Called from the RecyclerView adapter when a person is selected.
     * Checks if the person selected was correct
     * @param person
     */
    @Override
    public void personClicked(Items person) {
        if (curProfile.getId() == person.getId()) {
            correctChoice();
        } else {
            incorrectChoice();
        }
    }

    /**
     * Shows a snackbar with provided message
     * @param message
     */
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .show();
    }


    /**
     * Saves the mode
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.MODE, mode);
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Sets the mode
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        updateView();
        mode = (Mode) savedInstanceState.get(Constants.MODE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * When screen is rotated and rebuilt, updates the view to show the correct information
     */
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

    /**
     * hides progress dialog so it is not leaked
     */
    @Override
    protected void onDestroy() {
        DialogManager.hideProgressDialog();
        super.onDestroy();
    }

    /**
     * Creates menu with different modes
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_name_game, menu);
        return true;
    }

    /**
     * Starts a new game with the correct mode set
     * @param item
     * @return
     */
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

    /**
     * Shows a dialog with the correct answer
     */
    private void showCorrectAnswerDialog(){
        if(curProfile != null) {
            String name = curProfile.getFirstName() + " " + curProfile.getLastName();
            String url = curProfile.getHeadshot().getUrl();
            CorrectAnswerDialog dialog = CorrectAnswerDialog.getInstance(name, url);
            dialog.show(getSupportFragmentManager(), Constants.ANSWER_DIALOG_NAME);
        }
    }

    /**
     * Checks to see if current score is a highscore
     * @return
     */
    private boolean isHighscore(){
        SharedPreferences sharedPref = getSharedPreferences(
                Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int highscore = sharedPref.getInt(Constants.HIGHSCORE, 0);
        if(numCorrect > highscore){
            return true;
        }
        return false;
    }

    /**
     * Saves the score to SharedPreferences if it is a highscore
     */
    private void saveHighscore(){
        if(isHighscore()) {
            SharedPreferences sharedPref = getSharedPreferences(
                    Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.HIGHSCORE, numCorrect);
            editor.commit();
        }
    }
}
