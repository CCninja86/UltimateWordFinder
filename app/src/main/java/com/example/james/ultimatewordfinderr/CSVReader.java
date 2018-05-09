package com.example.james.ultimatewordfinderr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by james on 21/01/2017.
 */

public class CSVReader {

    Context context;


    public CSVReader(Context context) {
        this.context = context;
    }

    public ArrayList<Word> readFile(String filename, ProgressDialog progressDialog) {
        ArrayList<Word> wordList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        int numRows = 0;
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                numRows++;
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
            }
        }

        progressDialog.setMax(numRows);
        int progress = 0;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                progress++;
                progressDialog.setProgress(progress);
                Word word = new Word();
                String[] values = line.split(",");
                word.setId(Integer.parseInt(values[0]));
                word.setWord(values[1].substring(1, values[1].length() - 1));
                word.setBaseScore(Integer.parseInt(values[2]));

                if (Integer.parseInt(values[3]) == 0) {
                    word.setWordIsOfficial(false);
                } else {
                    word.setWordIsOfficial(true);
                }

                wordList.add(word);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
            }
        }

        return wordList;
    }

}
