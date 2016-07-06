package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by James on 19/11/2015.
 */
public class DatabaseHandler extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wordDatabase.db";
    private static final String TABLE_WORDS = "words";
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_BASE_SCORE = "word_base_score";
    private static final String KEY_IS_OFFICAL = "word_is_official";
    private Context context;
    private String currentWord;
    private int reconnectTimer;
    private int reconnectCurrent;
    private int currentWebpageLine;
    private int initialisationTimer;
    private int initialisationCurrent;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_WORDS);
        onCreate(sqLiteDatabase);
    }

    public void addWord(Word word) {
        boolean wordExists = false;

        if (this.getWord(word.getWord()) != null) {
            wordExists = true;
        }

        if (!wordExists) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ID, word.getId());
            values.put(KEY_WORD, word.getWord());
            values.put(KEY_BASE_SCORE, word.getBaseScore());
            values.put(KEY_IS_OFFICAL, word.isWordOfficial());

            db.insert(TABLE_WORDS, null, values);
            db.close();
        }
    }

    public Word getWord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORDS, new String[]{KEY_ID, KEY_WORD, KEY_BASE_SCORE, KEY_IS_OFFICAL}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Word word = new Word(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Boolean.parseBoolean(cursor.getString(3)));
            return word;
        }

        return null;
    }

    public Word getWord(String word) {
        Word matchedWord = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, word, word_base_score, word_is_official FROM words WHERE word LIKE '" + word + "'", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int wordId = Integer.parseInt(cursor.getString(0));
            String foundWord = cursor.getString(1);
            int baseScore = Integer.parseInt(cursor.getString(2));
            boolean isOfficial = false;

            if(Integer.parseInt(cursor.getString(3)) == 1){
                isOfficial = true;
            }

            matchedWord = new Word(wordId, foundWord, baseScore, isOfficial);
        }

        return matchedWord;
    }

    public ArrayList<Word> getAllWords(ProgressDialog progressDialog) {
        ArrayList<Word> wordList = new ArrayList<>();

        String selectQuery = "SELECT id, word, word_base_score, word_is_official FROM " + TABLE_WORDS + " ORDER BY word ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        progressDialog.setMax(cursor.getCount());
        int progress = 0;


        if (cursor.moveToFirst()) {
            do {
                progress++;
                progressDialog.setProgress(progress);
                Word word = new Word();
                word.setId(Integer.parseInt(cursor.getString(0)));
                word.setWord(cursor.getString(1));
                word.setBaseScore(Integer.parseInt(cursor.getString(2)));

                boolean wordIsOfficial = false;

                if(cursor.getString(3).equals("1")){
                    wordIsOfficial = true;
                }

                word.setWordIsOfficial(wordIsOfficial);
                wordList.add(word);
            } while (cursor.moveToNext());
        }

        return wordList;
    }

    public int getWordsCount() {
        String countQuery = "SELECT * FROM " + TABLE_WORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }



    public void insertAllWords(AssetManager assetManager, Dictionary dictionary, final ProgressBar progressBar) {
        initialisationCurrent = 0;
        initialisationTimer = 30;

        while(initialisationCurrent < initialisationTimer){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textViewRestartTimer = (TextView) ((Activity) context).findViewById(R.id.textViewRestartTimer);
                    textViewRestartTimer.setText("Restarting in " + (initialisationTimer - initialisationCurrent) + " seconds");
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            initialisationCurrent++;
        }



        InputStream inputStream = null;

        try {
            inputStream = assetManager.open("words.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final TextView textViewWebpageProgress = (TextView) ((Activity) context).findViewById(R.id.textViewWebpage);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int i = 649104;

            while ((line = bufferedReader.readLine()) != null) {
                int id = i;
                currentWord = line;
                if (!currentWord.contains("'")) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) ((Activity) context).findViewById(R.id.textViewWordProgress);
                            textView.setText("Inserting Word: " + currentWord);
                        }
                    });

                    if (this.getWord(currentWord) == null) {
                        int baseWordScore = dictionary.getBaseWordScore(currentWord);
                        boolean isWordOffical = false;
                        URL url = new URL("http://www.wordfind.com/word/" + currentWord + "/");



                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewWebpageProgress.setText("Checking Webpage: Connecting...");
                            }
                        });

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.connect();
                        int responseCode = httpURLConnection.getResponseCode();

                        while (responseCode == 404) {
                            httpURLConnection.disconnect();
                            reconnectTimer = 30;
                            reconnectCurrent = 0;

                            while (reconnectCurrent < reconnectTimer) {
                                Thread.sleep(1000);
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textView = (TextView) ((Activity) context).findViewById(R.id.textViewReconnect);
                                        textView.setText("Reconnecting in " + (reconnectTimer - reconnectCurrent) + "seconds");
                                    }
                                });
                                reconnectCurrent++;
                            }

                            httpURLConnection.connect();
                            responseCode = httpURLConnection.getResponseCode();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                        String websiteLine;
                        currentWebpageLine = 0;

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewWebpageProgress.setText("Checking Webpage: Reading lines...");
                            }
                        });

                        while ((websiteLine = in.readLine()) != null) {
                            if (websiteLine.contains("Yes!") || websiteLine.contains("It's still good as a Scrabble word though!")) {
                                isWordOffical = true;
                            }
                            currentWebpageLine++;
                        }

                        Log.i("Progress", "Finished checking webpage for '" + currentWord + "'...");

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewWebpageProgress.setText("Checking Webpage: Done!");
                            }
                        });

                        this.addWord(new Word(id, currentWord, baseWordScore, isWordOffical));
                        i++;
                    }

                }

                close();

            }

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewWebpageProgress.setText("Progress: Finished adding all words to database!");
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "FileNotFoundException thrown, restarting...");
            this.insertAllWords(assetManager, dictionary, progressBar);
        } catch (IOException e) {
            Log.e("IOException", "IOException thrown, restarting...");
            this.insertAllWords(assetManager, dictionary, progressBar);
        } catch (InterruptedException e) {
            Log.e("InterruptedException", "InterruptedException thrown, restarting...");
            this.insertAllWords(assetManager, dictionary, progressBar);
        } catch (Exception e) {
            Log.e("Other Error", "Other Exception thrown, printing stack trace...");
            e.printStackTrace();
        }
    }
}
