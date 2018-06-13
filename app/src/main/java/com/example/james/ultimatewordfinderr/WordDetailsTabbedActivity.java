package com.example.james.ultimatewordfinderr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.blogc.android.views.ExpandableTextView;
import nz.co.ninjastudios.datamuseandroid.DatamuseAndroid;
import nz.co.ninjastudios.datamuseandroid.DatamuseAndroidResultsListener;
import nz.co.ninjastudios.datamuseandroid.Word;

public class WordDetailsTabbedActivity extends AppCompatActivity implements WordDetailsDefinitionsTabFragment.OnFragmentInteractionListener, WordDetailsSynonymsTabFragment.OnFragmentInteractionListener, DatamuseAndroidResultsListener {

    private DatamuseAndroid datamuseAndroid;

    private TextView textViewWord;
    private TextView textViewPartOfSpeech;
    private TextView textViewPronunciation;
    private SpeedDialView buttonViewMore;
    private TabLayout tabLayout;

    private boolean definitionsExpandable;
    private boolean expand;

    private ProgressDialog progressDialog;

    private Map<String, String> partsOfSpeechMap;

    private Context context;

    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        // Disable Toolbar scrolling so the toolbar plays nice with the Lists
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        toolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(null);
        appBarLayout.setLayoutParams(appBarLayoutParams);


        word = getIntent().getStringExtra("word");

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), word);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        context = this;

        partsOfSpeechMap = new HashMap<>();
        partsOfSpeechMap.put("n", "noun");
        partsOfSpeechMap.put("v", "verb");
        partsOfSpeechMap.put("adj", "adjective");
        partsOfSpeechMap.put("adv", "adverb");
        partsOfSpeechMap.put("u", "unknown");

        textViewWord = findViewById(R.id.textViewWord);
        textViewPartOfSpeech = findViewById(R.id.textViewPartOfSpeech);
        textViewPronunciation = findViewById(R.id.textViewPronunciation);
        buttonViewMore = findViewById(R.id.fabViewMore);

        textViewPartOfSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder partsOfSpeechLegend = new AlertDialog.Builder(context);
                partsOfSpeechLegend.setTitle("Parts of Speech Legend");

                StringBuilder partsOfSpeechMapStringBuilder = new StringBuilder();

                for (Map.Entry entry : partsOfSpeechMap.entrySet()) {
                    partsOfSpeechMapStringBuilder.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
                }

                partsOfSpeechLegend.setMessage(partsOfSpeechMapStringBuilder.toString());

                partsOfSpeechLegend.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                partsOfSpeechLegend.show();
            }
        });

        textViewWord.setText(word);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting word details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        datamuseAndroid = new DatamuseAndroid(true);
        datamuseAndroid.setResultsListener(this);
        datamuseAndroid.spelledLike(word);
        datamuseAndroid.setMetadataFlags(new String[]{"p", "r"});
        datamuseAndroid.maxResults(1);
        datamuseAndroid.get();


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_details_tabbed, menu);
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
    public void onResultsSuccess(ArrayList<Word> words) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (words.size() > 0) {
            Word word = words.get(0);

            String[] tags = word.getTags();
            String pronunciationString = "";
            StringBuilder partOfSpeechStringBuilder = new StringBuilder();

            for (String tag : tags) {
                if (tag.contains("ipa_pron")) {
                    pronunciationString = tag.split(":")[1];
                }

                if (!tag.contains(":")) {
                    partOfSpeechStringBuilder.append(tag).append(",");
                }
            }

            if (partOfSpeechStringBuilder.toString().length() > 0) {
                partOfSpeechStringBuilder.insert(0, new char[]{'('}, 0, 1);
                partOfSpeechStringBuilder.replace(partOfSpeechStringBuilder.toString().lastIndexOf(","), partOfSpeechStringBuilder.toString().lastIndexOf(",") + 1, "");
                partOfSpeechStringBuilder.append(")");

                textViewPartOfSpeech.setText(partOfSpeechStringBuilder.toString());
            } else {
                textViewPartOfSpeech.setText("[u]");
            }

            textViewPronunciation.setText(pronunciationString);
        } else {
            Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public void onFragmentInteraction(String word, ArrayList<Word> synonyms) {

    }

    @Override
    public void onFragmentInteraction(String word, DefinitionList definitionList) {

    }

    @Override
    public void onFragmentInteraction(String option) {

    }

    @Override
    public void onSynonymsResultsLoaded(int numResults) {
        tabLayout.getTabAt(1).setText(tabLayout.getTabAt(1).getText().toString() + " (" + numResults + ")");
    }

    @Override
    public void onDefinitionsResultsLoaded(int numResults) {
        tabLayout.getTabAt(0).setText(tabLayout.getTabAt(0).getText().toString() + " (" + numResults + ")");
    }
}
