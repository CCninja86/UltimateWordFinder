package com.example.james.ultimatescrabbleapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;

public class WordFinderActivity extends AppCompatActivity implements WordFinderMainFragment.OnFragmentInteractionListener, AdvancedSearchFragment.OnFragmentInteractionListener, WordFinderScoreComparisonFragment.OnFragmentInteractionListener, WordFinderDictionaryFragment.OnFragmentInteractionListener, WordDefinitionFragment.OnFragmentInteractionListener, SynonymResultListFragment.OnFragmentInteractionListener, WordFinderSearchResultsFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener, BugReportFragment.OnFragmentInteractionListener {


    private com.example.james.ultimatescrabbleapp.Dictionary dictionary;
    private WordFinderMainFragment wordFinderMainFragment;
    private WordFinderDictionaryFragment wordFinderDictionaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_finder);

        Bundle bundle = getIntent().getBundleExtra("selection");
        int selection = bundle.getInt("selection");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(savedInstanceState == null) {
            if (selection == 1) {
                wordFinderDictionaryFragment = new WordFinderDictionaryFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, wordFinderDictionaryFragment);
            } else {
                wordFinderMainFragment = new WordFinderMainFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, wordFinderMainFragment);
            }

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
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containerWordFinder, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment wordDefinitionFragment = new WordDefinitionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("word", selectedWords.get(0));
            wordDefinitionFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.containerWordFinder, wordDefinitionFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
    public void onResultsFragmentInteraction(DefinitionList definitionList) {
        loadDefinitionsFragment(definitionList);
    }

    @Override
    public void onResultsFragmentInteraction(ArrayList<String> synonyms) {
        loadSynonymsFragment(synonyms);
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        if(wordFinderMainFragment != null) {
            wordFinderMainFragment.backButtonWasPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDictionaryFragmentInteraction(DefinitionList definitionList) {
        loadDefinitionsFragment(definitionList);
    }

    @Override
    public void onDictionaryFragmentInteraction(ArrayList<String> synonyms) {
        loadSynonymsFragment(synonyms);
    }

    private void loadSynonymsFragment(ArrayList<String> synonyms){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment synonymFragment = new SynonymResultListFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Synonyms", synonyms);
        synonymFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, synonymFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadDefinitionsFragment(DefinitionList definitionList){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment wordDefinitionFragment = new WordDefinitionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Definition List", new Gson().toJson(definitionList));
        wordDefinitionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, wordDefinitionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String option) {
        switch (option){
            case "Report Bug":
                BugReportFragment bugReportFragment = new BugReportFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerWordFinder, bugReportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }
    }
}
