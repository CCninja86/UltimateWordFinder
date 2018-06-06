package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nz.co.ninjastudios.datamuseandroid.DatamuseAndroid;
import nz.co.ninjastudios.datamuseandroid.DatamuseAndroidResultsListener;

/**
 * Created by james on 23/02/2017.
 */

public class WordOptionsHandler implements DatamuseAndroidResultsListener {

    private Context context;
    private static String word;
    private DefinitionList definitionList;
    private ArrayList<String> synonyms;
    private static final int MAX_ATTEMPTS = 5;

    private DatamuseAndroid datamuseAndroid;
    private static DatamuseAndroidResultsListener datamuseAndroidResultsListener;
    private WordOptionsHandlerResultsListener wordOptionsHandlerResultsListener;

    private GetDefinitionsTask getDefinitionsTask;
    private GetSynonymsTask getSynonymsTask;

    WordOptionsHandler(WordOptionsHandlerResultsListener resultsListener, Context context, String word) {
        WordOptionsHandler.word = word;
        this.context = context;
        this.wordOptionsHandlerResultsListener = resultsListener;
        this.definitionList = new DefinitionList();
        this.synonyms = new ArrayList<>();
        WordOptionsHandler.datamuseAndroidResultsListener = this;

        datamuseAndroid = new DatamuseAndroid(true);
        datamuseAndroid.setResultsListener(datamuseAndroidResultsListener);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void loadDefinitions() {
        getDefinitionsTask = new GetDefinitionsTask();
        getDefinitionsTask.execute();
    }

    public void loadSynonyms() {
        getSynonymsTask = new GetSynonymsTask();
        getSynonymsTask.execute();
    }

    public void cancelExecution(){
        if(getDefinitionsTask != null && !getDefinitionsTask.isCancelled()){
            getDefinitionsTask.cancel(true);
        }

        if(getSynonymsTask != null && !getSynonymsTask.isCancelled()){
            getSynonymsTask.cancel(true);
        }
    }

    @Override
    public void onResultsSuccess(ArrayList<nz.co.ninjastudios.datamuseandroid.Word> words) {
        if(words.size() == 1 && words.get(0).getDefs().length > 0){
            DefinitionList definitionList = new DefinitionList();

            for(String definition : words.get(0).getDefs()){
                Definition wordDefinition = new Definition();
                wordDefinition.setPartOfSpeech(definition.split("\t")[0]);
                wordDefinition.setDefinition(definition.split("\t")[1]);

                definitionList.addDefinition(wordDefinition);
            }

            wordOptionsHandlerResultsListener.onDefinitionsSuccess(word, definitionList);
        } else {
            wordOptionsHandlerResultsListener.onSynonymsSuccess(word, words);
        }


    }

    private class GetDefinitionsTask extends AsyncTask<Void, Void, Void> {

        public GetDefinitionsTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(!isCancelled()){
                datamuseAndroid.spelledLike(word);
                datamuseAndroid.setMetadataFlags(new String[]{"d"});
                datamuseAndroid.maxResults(1);
                datamuseAndroid.get();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new CheckOfficialStatusTask(word).execute();
        }
    }

    private class GetSynonymsTask extends AsyncTask<Object, Void, Void> {

        public GetSynonymsTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Object... params) {
            if(!isCancelled()){
                if (word.contains(" ")) {
                    word = word.toLowerCase().replaceAll(" ", "%20");
                }

                datamuseAndroid.synonymsOf(word);
                datamuseAndroid.get();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

    private class CheckOfficialStatusTask extends AsyncTask<Void, Void, Void> {

        private String word;
        private String url;
        private boolean official;

        public CheckOfficialStatusTask(String word){
            this.word = word;
            this.url = "https://wordfind.com/word/" + word;
            this.official = false;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document document = Jsoup.connect(url).get();

                Elements resultElements = document.select("p.letters > span");

                if(resultElements.size() > 0 && resultElements.get(0) != null){
                    if(resultElements.get(0).text().equals("Yes!")){
                        official = true;
                    }
                }

                // TODO: Make request to future custom backend to update status
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }
}