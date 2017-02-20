package com.example.james.ultimatescrabbleapp;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class ScoringTableActivity extends AppCompatActivity implements ScoringFragment.OnFragmentInteractionListener, PlayerDetailsFragment.OnFragmentInteractionListener,
        AddWordsFragment.OnFragmentInteractionListener, WordHistoryFragment.OnFragmentInteractionListener,
        ScoreDisplayFragment.OnFragmentInteractionListener, TileBreakdownFragment.OnFragmentInteractionListener, RemoveAdsFragment.OnFragmentInteractionListener {

    private ArrayList<String> players;
    private Scrabble scrabbleGame;
    private Globals g;

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring_table);

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

//        bundle = new Bundle();
//        bundle.putSerializable("Scrabble Game", scrabbleGame);

        if(savedInstanceState == null) {
            Fragment scoringFragment = new ScoringFragment();
            //scoringFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.itemRemoveAds:
                RemoveAdsFragment removeAdsFragment = new RemoveAdsFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, removeAdsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onScoringFragmentListInteraction(Player player, Scrabble scrabbleGame) {
        Fragment playerDetailsFragment = new PlayerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Player", player);
        playerDetailsFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, playerDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScoringFragmentButtonInteraction(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (action){
            case "Add Score":
                Fragment addWordsFragment = new AddWordsFragment();
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
                        int newScore = Integer.parseInt(input.getText().toString());
                        player.setScore(newScore);
                        Toast.makeText(getBaseContext(), "Score Changed!", Toast.LENGTH_LONG).show();
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
                        player.setName(newName);
                        Toast.makeText(getBaseContext(), "Player Name Changed!", Toast.LENGTH_LONG).show();
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
}
