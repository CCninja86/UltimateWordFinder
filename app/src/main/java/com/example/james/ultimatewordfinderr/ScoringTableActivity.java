package com.example.james.ultimatewordfinderr;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ScoringTableActivity extends Activity implements ScoringFragment.OnFragmentInteractionListener, PlayerDetailsFragment.OnFragmentInteractionListener,
        AddWordsFragment.OnFragmentInteractionListener, WordHistoryFragment.OnFragmentInteractionListener,
        ScoreDisplayFragment.OnFragmentInteractionListener, TileBreakdownFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener, BugReportFragment.OnFragmentInteractionListener {

    private ArrayList<String> players;
    private Scrabble scrabbleGame;
    private Globals g;

    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle drawerToggle;

    private Dictionary dictionary;
    private boolean hasActiveInternetConnection;

    private static final int DICTIONARY = 1;
    private static final int WORD_FINDER = 2;
    private int selection;

    private Context context;



    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring_table);

        context = this;
        mTitle = mDrawerTitle = getTitle();

        drawerItems = getResources().getStringArray(R.array.drawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavDrawerListAdapter(this, drawerItems, R.layout.drawer_list_item));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);

        g = Globals.getInstance();
        Bundle bundle = getIntent().getExtras();
        this.players = bundle.getStringArrayList("Player List");


        scrabbleGame = new Scrabble();
        scrabbleGame.initialiseTiles();

        for (String playerName : players) {
            Player player = new Player(playerName, scrabbleGame);
            scrabbleGame.addPlayer(player);
        }

        g.setGame(scrabbleGame);

        bundle = new Bundle();
        bundle.putSerializable("Scrabble Game", scrabbleGame);

        if(savedInstanceState == null) {
            Fragment scoringFragment = new ScoringFragment();
            scoringFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, scoringFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemHelpFeedback:
                HelpFeedbackFragment helpFeedbackFragment = new HelpFeedbackFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScoringFragmentListInteraction(Player player, Scrabble scrabbleGame) {
        PlayerDetailsFragment playerDetailsFragment = new PlayerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Player", player);
        playerDetailsFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, playerDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScoringFragmentButtonInteraction(View view){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.btnShowScores:
                Fragment scoreDisplayFragment = new ScoreDisplayFragment();
                Bundle scoreDisplayBundle = new Bundle();
                scoreDisplayBundle.putSerializable("Scrabble Game", scrabbleGame);
                scoreDisplayFragment.setArguments(scoreDisplayBundle);
                fragmentTransaction.replace(R.id.container, scoreDisplayFragment);
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.btnTileBreakdown:
                Fragment tileBreakdownFragment = new TileBreakdownFragment();
                Bundle tileBreakdownBundle = new Bundle();
                tileBreakdownBundle.putSerializable("Scrabble Game", scrabbleGame);
                tileBreakdownFragment.setArguments(tileBreakdownBundle);
                fragmentTransaction.replace(R.id.container, tileBreakdownFragment);
                fragmentTransaction.addToBackStack(null);
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onPlayerDetailsFragmentInteraction(String action, final Player player) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (action){
            case "Add Score":
                AddWordsFragment addWordsFragment = new AddWordsFragment();
                Bundle addWordsBundle = new Bundle();
                addWordsBundle.putSerializable("Player", player);
                addWordsFragment.setArguments(addWordsBundle);
                fragmentTransaction.replace(R.id.container, addWordsFragment);
                fragmentTransaction.addToBackStack(null);
                break;
            case "Word History":
                Fragment wordHistoryFragment = new WordHistoryFragment();
                Bundle wordHistoryBundle = new Bundle();
                wordHistoryBundle.putSerializable("Player", player);
                wordHistoryFragment.setArguments(wordHistoryBundle);
                fragmentTransaction.replace(R.id.container, wordHistoryFragment);
                fragmentTransaction.addToBackStack(null);
                break;
            case "Amend Score":
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Amend Score");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Change Score", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String enteredInput = input.getText().toString();

                        if(enteredInput != null && !enteredInput.equals("")){
                            int newScore = Integer.parseInt(input.getText().toString());
                            player.setScore(newScore);
                            Toast.makeText(getBaseContext(), "Score Changed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();

                break;
            case "Change Player Name":
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Change Player Name");
                final EditText nameInput = new EditText(this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                alertBuilder.setView(nameInput);

                alertBuilder.setPositiveButton("Change Player Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = nameInput.getText().toString();

                        if(newName != null && !newName.equals("")){
                            player.setName(newName);
                            Toast.makeText(getBaseContext(), "Player Name Changed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alertBuilder.show();

                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onAddWordsFragmentInteraction(View view) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch(view.getId()){
            case R.id.btnAddWordScore:
                Fragment scoringFragment = new ScoringFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Scrabble Game", scrabbleGame);
                scoringFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, scoringFragment);
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String option) {
        switch (option){
            case "Report Bug":
                BugReportFragment bugReportFragment = new BugReportFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, bugReportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (position){
            case 0:
                ScoringFragment scoringFragment = new ScoringFragment();
                fragmentTransaction.replace(R.id.container, scoringFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

                break;
            case 1:
                ScoreDisplayFragment scoreDisplayFragment = new ScoreDisplayFragment();
                Bundle scoreDisplayBundle = new Bundle();
                scoreDisplayBundle.putSerializable("Scrabble Game", scrabbleGame);
                scoreDisplayFragment.setArguments(scoreDisplayBundle);
                fragmentTransaction.replace(R.id.container, scoreDisplayFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

                break;
            case 2:
                TileBreakdownFragment tileBreakdownFragment = new TileBreakdownFragment();
                Bundle tileBreakdownBundle = new Bundle();
                tileBreakdownBundle.putSerializable("Scrabble Game", scrabbleGame);
                tileBreakdownFragment.setArguments(tileBreakdownBundle);
                fragmentTransaction.replace(R.id.container, tileBreakdownFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

                break;
            case 3:
                loadWordFinderActivity("Word Finder");
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

                break;
            case 4:
                loadWordFinderActivity("Dictionary");
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

                break;


        }
    }

    private void loadWordFinderActivity(final String fragment){
        if(dictionary == null) {
            final AlertDialog.Builder builderConfirm = new AlertDialog.Builder(this);
            builderConfirm.setTitle("First-time Setup");
            builderConfirm.setMessage("Both the Dictionary and Word Finder features utilise a large database of words. " +
                    "Due to this, a first-time setup is required to use these features. This setup may take a while, " +
                    "depending on the speed of your device. Would you like to perform the first-time setup now?");

            builderConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builderConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoadFragmentTask task = new LoadFragmentTask(fragment);
                    task.execute();
                }
            });

            builderConfirm.show();
        } else {
            LoadFragmentTask task = new LoadFragmentTask(fragment);
            task.execute();
        }
    }

    @Override
    public void setTitle(CharSequence title){
        mTitle = title;
        getActionBar().setTitle(title);
    }

    private class LoadFragmentTask extends AsyncTask<Void, Void, Void> {

        private String fragmentToLoad;
        private ProgressDialog progressDialog;

        public LoadFragmentTask(String fragmentToLoad){
            this.fragmentToLoad = fragmentToLoad;
        }

        @Override
        protected void onPreExecute(){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Performing first-time setup...");
            progressDialog.setMessage("Loading Dictionary...");
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(dictionary == null){
                dictionary = new com.example.james.ultimatewordfinderr.Dictionary();
                final CSVReader csvReader = new CSVReader(context);
                dictionary.linkCSVReader(csvReader);
                dictionary.setWordList(progressDialog);
                g.setDictionary(dictionary);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if(fragmentToLoad.equals("Word Finder")){
                selection = WORD_FINDER;
            } else if(fragmentToLoad.equals("Dictionary")){
                selection = DICTIONARY;
            }

            setup();
        }
    }

    private void setup(){
        Bundle bundle = new Bundle();
        bundle.putInt("selection", selection);
        Intent intent = new Intent(this, WordFinderActivity.class);
        intent.putExtra("selection", bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean hasActiveInternetConnection(){
        boolean success = false;

        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);

        return super.onPrepareOptionsMenu(menu);
    }
}
