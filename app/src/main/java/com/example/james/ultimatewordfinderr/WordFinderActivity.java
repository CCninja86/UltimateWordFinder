package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WordFinderActivity extends AppCompatActivity implements WordFinderMainFragment.OnFragmentInteractionListener, AdvancedSearchFragment.OnFragmentInteractionListener, WordFinderScoreComparisonFragment.OnFragmentInteractionListener, WordFinderDictionaryFragment.OnFragmentInteractionListener, WordDetailsDefinitionsTabFragment.OnFragmentInteractionListener, WordDetailsSynonymsTabFragment.OnFragmentInteractionListener, WordFinderSearchResultsFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener, BugReportFragment.OnFragmentInteractionListener {


    private com.example.james.ultimatewordfinderr.Dictionary dictionary;
    private WordFinderMainFragment wordFinderMainFragment;
    private WordFinderDictionaryFragment wordFinderDictionaryFragment;

    private Toolbar toolbar;

    private LinkedHashMap<String, Integer> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_finder);

        this.searchResults = new LinkedHashMap<>();

        toolbar = (Toolbar) findViewById(R.id.word_finder_activity_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getBundleExtra("selection");
        int selection = bundle.getInt("selection");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
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

    public void showFilterButton(){
        toolbar.getMenu().findItem(R.id.filter).setVisible(true);
    }

    public void hideFilterButton(){
        toolbar.getMenu().findItem(R.id.filter).setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_finder_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.filter);
        menuItem.setVisible(false);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                hideFilterButton();

                AdvancedSearchFragment advancedSearchFragment = new AdvancedSearchFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerWordFinder, advancedSearchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onSearchFragmentInteraction(View view, LinkedHashMap<String, Integer> searchMatches) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.btnSearch:

                WordFinderSearchResultsFragment searchResultsFragment = new WordFinderSearchResultsFragment();

                Gson gson = new Gson();
                String mapJson = gson.toJson(searchMatches, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());

                Bundle bundle = new Bundle();
                bundle.putString("Search Results", mapJson);
                searchResultsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
                fragmentTransaction.addToBackStack(null);

                break;
        }

        fragmentTransaction.commit();
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        for(Map.Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public void onResultsFragmentButtonInteraction(String action, ArrayList<String> selectedWords) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (action) {
            case "definition": {
                WordDetailsDefinitionsTabFragment wordDetailsDefinitionsTabFragment = new WordDetailsDefinitionsTabFragment();
                Bundle bundle = new Bundle();
                bundle.putString("word", selectedWords.get(0));
                wordDetailsDefinitionsTabFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, wordDetailsDefinitionsTabFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            case "compare": {
                WordFinderScoreComparisonFragment scoreComparisonFragment = new WordFinderScoreComparisonFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("wordsToCompare", selectedWords);
                scoreComparisonFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, scoreComparisonFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            case "details":
                Intent intent = new Intent(this, WordDetailsTabbedActivity.class);
                intent.putExtra("word", selectedWords.get(0));
                startActivity(intent);

                break;
        }
    }

    @Override
    public void onResultsFragmentInteraction(String word, DefinitionList definitionList) {
        loadDefinitionsFragment(word, definitionList);
    }

    @Override
    public void onResultsFragmentInteraction(String word, ArrayList<nz.co.ninjastudios.datamuseandroid.Word> synonyms) {
        loadSynonymsFragment(word, synonyms);
    }

    @Override
    public void onResultsFragmentLoaded(LinkedHashMap<String, Integer> searchResults) {
        this.searchResults = searchResults;
        showFilterButton();
    }

    @Override
    public void onResultsFragmentClosed() {
        hideFilterButton();
    }

    @Override
    public void onAdvancedSearchFragmentInteraction(View view, Map<String, String> filters) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WordFinderSearchResultsFragment searchResultsFragment = new WordFinderSearchResultsFragment();
        LinkedHashMap<String, Integer> words = new LinkedHashMap<>();

        for (Map.Entry entry : searchResults.entrySet()) {
            String word = (String) entry.getKey();
            int wordScore = (int) entry.getValue();
            boolean matchesFilters = false;

            int minWordLength = Integer.parseInt(filters.get("Minimum Word Length"));
            int maxWordLength = Integer.parseInt(filters.get("Maximum Word Length"));

            if(word.contains(filters.get("Contains"))
                    && word.startsWith(filters.get("Prefix"))
                    && word.endsWith(filters.get("Suffix"))){

                if(maxWordLength != 0){
                    if(word.length() > minWordLength && word.length() < maxWordLength){
                        matchesFilters = true;
                    }
                } else if(minWordLength != 0){
                    if(word.length() > minWordLength){
                        matchesFilters = true;
                    }
                } else {
                    matchesFilters = true;
                }

            }

            if(matchesFilters){
                words.put(word, wordScore);
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(words, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());

        Bundle bundle = new Bundle();
        bundle.putString("Search Results", json);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScoreComparisonFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (wordFinderMainFragment != null) {
            wordFinderMainFragment.backButtonWasPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDictionaryFragmentInteraction(String word) {
        Intent intent = new Intent(this, WordDetailsTabbedActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    private void loadSynonymsFragment(String word, ArrayList<nz.co.ninjastudios.datamuseandroid.Word> synonyms) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WordDetailsSynonymsTabFragment wordDetailsSynonymsTabFragment = new WordDetailsSynonymsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Synonyms", synonyms);
        bundle.putString("Word", word);
        wordDetailsSynonymsTabFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, wordDetailsSynonymsTabFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadDefinitionsFragment(String word, DefinitionList definitionList) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WordDetailsDefinitionsTabFragment wordDetailsDefinitionsTabFragment = new WordDetailsDefinitionsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Definition List", new Gson().toJson(definitionList));
        bundle.putString("Word", word);
        wordDetailsDefinitionsTabFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, wordDetailsDefinitionsTabFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String option) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (option) {
            case "Report Bug":
                BugReportFragment bugReportFragment = new BugReportFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, bugReportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onSynonymsResultsLoaded(int numResults) {

    }

    @Override
    public void onFragmentInteraction(String word, ArrayList<nz.co.ninjastudios.datamuseandroid.Word> synonyms) {
        loadSynonymsFragment(word, synonyms);
    }

    @Override
    public void onFragmentInteraction(String word, DefinitionList definitionList) {
        loadDefinitionsFragment(word, definitionList);
    }

    @Override
    public void onDefinitionsResultsLoaded(int numResults) {

    }
}
