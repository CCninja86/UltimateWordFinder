package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
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

    private static DatamuseAndroidResultsListener datamuseAndroidResultsListener;
    private WordOptionsHandlerResultsListener wordOptionsHandlerResultsListener;

    WordOptionsHandler(WordOptionsHandlerResultsListener resultsListener, Context context, String word) {
        WordOptionsHandler.word = word;
        this.context = context;
        this.wordOptionsHandlerResultsListener = resultsListener;
        this.definitionList = new DefinitionList();
        this.synonyms = new ArrayList<>();
        WordOptionsHandler.datamuseAndroidResultsListener = this;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void loadDefinitions() {
        GetDefinitionsTask getDefinitionsTask = new GetDefinitionsTask();
        getDefinitionsTask.execute();
    }

    public void loadSynonyms() {
        GetSynonymsTask getSynonymsTask = new GetSynonymsTask();
        getSynonymsTask.execute();
    }

    @Override
    public void onResultsSuccess(ArrayList<nz.co.ninjastudios.datamuseandroid.Word> synonyms) {
        if (!synonyms.isEmpty()) {
            wordOptionsHandlerResultsListener.onSynonymsSuccess(word, synonyms);
        } else {
            Toast.makeText(context, "No synonyms found", Toast.LENGTH_SHORT).show();
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
            String searchUrl;
            ArrayList<String> definitions = new ArrayList<>();
            ArrayList<String> subjectHeaders = new ArrayList<>();
            int numAttempts = 0;

            while (numAttempts < MAX_ATTEMPTS) {
                try {
                    searchUrl = "http://www.dictionary.com/browse/" + word;
                    Document document = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0 (Linux; Android 5.1.1; Vodafone Smart ultra 6"
                            + " Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91"
                            + " Mobile Safari/537.36").referrer("http://www.google.com").get();


                    Elements definitionElements = document.getElementsByClass("_14Msm");


                    for (Element element : definitionElements) {
                        Elements childElements = element.children();

                        for (Element child : childElements) {
                            if (child.hasClass("dbox-italic")) {
                                String sublistHeader = child.text();
                                subjectHeaders.add(sublistHeader);
                            }
                        }

                        String definition = element.text();

                        if (!definition.startsWith("see:")) {
                            definitions.add(definition);
                        }
                    }

                    // Remove duplicates from sublist header array, and remove any false positives
                    Set<String> temp = new HashSet<>();
                    temp.addAll(subjectHeaders);
                    subjectHeaders.clear();
                    subjectHeaders.addAll(temp);

                    Iterator<String> iterator = subjectHeaders.iterator();

                    while (iterator.hasNext()) {
                        String header = iterator.next();

                        if (!Character.isUpperCase(header.charAt(0))) {
                            iterator.remove();
                        }
                    }

                    for (String definition : definitions) {
                        for (String header : subjectHeaders) {
                            if (definition.startsWith(header)) {
                                String subject = definition.substring(0, definition.indexOf("."));
                                String[] definitionSublist = null;

                                if (!definition.contains("etc.")) {
                                    definitionSublist = definition.split("\\.");
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder(definition);

                                    for (int i = 0; i < definition.length(); i++) {
                                        String character = String.valueOf(definition.charAt(i));

                                        if (character.equals(".")) {
                                            String trailingSubstring = definition.substring(i - 3, i);

                                            if (trailingSubstring.equals("etc")) {
                                                stringBuilder.setCharAt(i, ',');
                                                stringBuilder.setCharAt(i + 1, ' ');
                                            }
                                        }
                                    }

                                    definitionSublist = stringBuilder.toString().split("\\.");
                                }


                                for (String def : definitionSublist) {
                                    if (!subjectHeaders.contains(def + ".")) {
                                        Definition definitionObject = new Definition();
                                        definitionObject.setSubject(subject);
                                        definitionObject.setDefinition(def);

                                        if (!definitionList.containsDefinition(definition)) {
                                            definitionList.addDefinition(definitionObject);
                                        }
                                    }
                                }

                            }
                        }

                        if (!definitionList.containsDefinition(definition)) {
                            Definition definitionObject = new Definition();
                            definitionObject.setDefinition(definition);
                            definitionList.addDefinition(definitionObject);
                        }
                    }

                    Iterator<Definition> definitionIterator = definitionList.getDefinitions().iterator();

                    while (definitionIterator.hasNext()) {
                        Definition definition = definitionIterator.next();

                        if (subjectHeaders.contains(definition.getDefinition().substring(0, definition.getDefinition().indexOf(".") + 1))) {
                            definitionIterator.remove();
                        }
                    }

                    definitionList.trimStrings();

                    if (definitionList.getDefinitions().size() < 1) {
                        numAttempts++;
                    } else {
                        break;
                    }
                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", e.getMessage());
                } catch (IOException e) {
                    numAttempts++;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (definitionList != null) {
                if (definitionList.getDefinitions().size() > 0) {
                    wordOptionsHandlerResultsListener.onDefinitionsSuccess(word, definitionList);
                } else {
                    Toast.makeText(context, "No definitions found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No definitions found", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private static class GetSynonymsTask extends AsyncTask<Object, Void, Void> {

        public GetSynonymsTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Object... params) {
            String searchUrl;

            if (word.contains(" ")) {
                word = word.toLowerCase().replaceAll(" ", "%20");
            }

            new DatamuseAndroid(true).withResultsListener(datamuseAndroidResultsListener).synonymsOf(word).get();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }
}