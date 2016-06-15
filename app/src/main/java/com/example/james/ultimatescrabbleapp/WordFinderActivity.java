package com.example.james.ultimatescrabbleapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class WordFinderActivity extends AppCompatActivity implements WordFinderMainFragment.OnFragmentInteractionListener, WordFinderSearchResultsFragment.OnFragmentInteractionListener, AdvancedSearchFragment.OnFragmentInteractionListener, WordFinderScoreComparisonFragment.OnFragmentInteractionListener {


    private com.example.james.ultimatescrabbleapp.Dictionary dictionary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_finder);


        Fragment wordFinderFragment = new WordFinderMainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerWordFinder, wordFinderFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchFragmentInteraction(View view, ArrayList<Word> searchMatches) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.btnSearch:
                Fragment searchResultsFragment = new WordFinderSearchResultsFragment();
                ArrayList<String> words = new ArrayList<>();

                for(Word word : searchMatches){
                    words.add(word.getWord());
                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Search Results", words);
                searchResultsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.btnAdvancedSearch:
                Fragment advancedSearchFragment = new AdvancedSearchFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, advancedSearchFragment);
                fragmentTransaction.addToBackStack(null);
                break;

        }

        fragmentTransaction.commit();
    }

    @Override
    public void onResultsFragmentInteraction(String action, ArrayList<String> selectedWords) {
        if(action.equals("definition")){
            final Intent browserActivity = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.dictionary.com/browse/" + selectedWords.get(0)));
            startActivity(browserActivity);
        } else if(action.equals("compare")){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment scoreComparisonFragment = new WordFinderScoreComparisonFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("wordsToCompare", selectedWords);
            scoreComparisonFragment.setArguments(bundle);
            transaction.replace(R.id.containerWordFinder, scoreComparisonFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onAdvancedSearchFragmentInteraction(View view, ArrayList<Word> matches) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment searchResultsFragment = new WordFinderSearchResultsFragment();
        ArrayList<String> words = new ArrayList<>();

        for(Word word : matches){
            words.add(word.getWord());
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Search Results", words);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScoreComparisonFragmentInteraction(Uri uri) {

    }
}
