package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderMainFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private com.example.james.ultimatescrabbleapp.Dictionary dictionary;
    private ArrayList<Word> matches;
    private ArrayList<String> selectedWords;
    private ArrayList<String> advancedSearchTextFilters;
    private int numLetterFilter;

    private EditText editTextLettersBoard;
    private EditText editTextLettersRack;
    private CheckBox checkOnlyLettersRack;
    private CheckBox checkIncludeKnown;
    private TextView textViewWordProgress;
    private ProgressDialog progressDialog;

    private boolean textFlag;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordFinderMainFragment newInstance(String param1, String param2) {
        WordFinderMainFragment fragment = new WordFinderMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordFinderMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void backButtonWasPressed(){
        String prevText = editTextLettersRack.getText().toString();

        if(prevText.length() > 0){
            editTextLettersRack.setText(prevText.substring(0, prevText.length() - 2));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_finder_main, container, false);
        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        final DatabaseHandler database = new DatabaseHandler(getContext());


        this.matches = new ArrayList<Word>();
        this.selectedWords = new ArrayList<String>();
        this.advancedSearchTextFilters = new ArrayList<String>();
        this.numLetterFilter = 0;

        editTextLettersBoard = (EditText) view.findViewById(R.id.editTextLettersBoard);
        editTextLettersRack = (EditText) view.findViewById(R.id.editTextLettersRack);
        checkOnlyLettersRack = (CheckBox) view.findViewById(R.id.checkOnlyLettersRack);
        checkIncludeKnown = (CheckBox) view.findViewById(R.id.checkBoxIncludeKnown);
        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);
        Button btnAdvancedSearch = (Button) view.findViewById(R.id.btnAdvancedSearch);

        editTextLettersBoard.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textFlag){
                    textFlag = false;
                    return;
                } else {
                    textFlag = true;

                    if(s.equals("-")){
                        return;
                    } else {
                        if(editTextLettersBoard.getText().toString().length() > 1) {
                            String[] characters = editTextLettersBoard.getText().toString().split("");
                            String newString = "";

                            for (String character : characters) {
                                if(!character.equals("-")){
                                    newString += character + "-";
                                }
                            }

                            newString = newString.substring(0, newString.length() - 1);
                            newString = newString.substring(1, newString.length());

                            editTextLettersBoard.setText(newString);
                            editTextLettersBoard.setSelection(editTextLettersBoard.getText().length());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextLettersRack.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textFlag){
                    textFlag = false;
                    return;
                } else {
                    textFlag = true;

                    if(s.equals("-")){
                        return;
                    } else {
                        if(editTextLettersRack.getText().toString().length() > 1) {
                            String[] characters = editTextLettersRack.getText().toString().split("");
                            String newString = "";

                            for (String character : characters) {
                                if(!character.equals("-")){
                                    newString += character + "-";
                                }
                            }

                            newString = newString.substring(0, newString.length() - 1);
                            newString = newString.substring(1, newString.length());

                            editTextLettersRack.setText(newString);
                            editTextLettersRack.setSelection(editTextLettersRack.getText().length());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String[] lettersInInput = editTextLettersBoard.getText().toString().split("-");
                boolean filterTextEmpty = editTextLettersRack.getText().toString().isEmpty();
                SearchDictionaryTask task = new SearchDictionaryTask(lettersInInput, view, filterTextEmpty);
                task.execute();

            }
        });

        btnAdvancedSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mListener.onSearchFragmentInteraction(view, null);
            }
        });

        checkIncludeKnown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String[] lettersOnBoard = editTextLettersBoard.getText().toString().split("-");
                    ArrayList<String> lettersToInclude = new ArrayList<>();

                    for(String letter : lettersOnBoard){
                        if(!letter.equals("?")){
                            lettersToInclude.add(letter);
                        }
                    }

                    for(String toInclude : lettersToInclude){
                        editTextLettersRack.setText(editTextLettersRack.getText().toString() + "-" + toInclude);
                    }
                } else {
                    int numKnownLetters = 0;

                    for(String letter : editTextLettersBoard.getText().toString().split("-")){
                        if(!letter.equals("?")){
                            numKnownLetters++;
                        }
                    }

                    if(numKnownLetters > 0){
                        String newString = editTextLettersRack.getText().toString();
                        newString = newString.substring(0, newString.length() - (numKnownLetters * 2));
                        editTextLettersRack.setText(newString);
                    }
                }
            }
        });

        // Dev Tools - inserting entries into database

        Button btnPopulateDatabase = (Button) view.findViewById(R.id.btnPopulateDatabase);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressDatabase);
        final TextView textViewWordProgress = (TextView) view.findViewById(R.id.textViewWordProgress);
        final TextView textViewReconnect = (TextView) view.findViewById(R.id.textViewReconnect);
        final TextView textViewRestart = (TextView) view.findViewById(R.id.textViewRestartTimer);
        final TextView webpageProgress = (TextView) view.findViewById(R.id.textViewWebpage);
        progressBar.setMax(354937);

        btnPopulateDatabase.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textViewWordProgress.setVisibility(View.INVISIBLE);
        textViewReconnect.setVisibility(View.INVISIBLE);
        textViewRestart.setVisibility(View.INVISIBLE);
        webpageProgress.setVisibility(View.INVISIBLE);

        btnPopulateDatabase.setEnabled(false);
        progressBar.setEnabled(false);
        textViewWordProgress.setEnabled(false);
        textViewReconnect.setEnabled(false);
        textViewRestart.setEnabled(false);
        webpageProgress.setEnabled(false);

        btnPopulateDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AssetManager assetManager = getContext().getAssets();
                        database.insertAllWords(assetManager, dictionary, progressBar);
                    }
                }).start();
            }
        });

        // Dev Tools - inserting entries into database






        return view;
    }

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//data//" + "nz.ac.aut.ultimatescrabbleapp"
                        + "//databases//" + "databases/wordDatabase";
                String backupDBPath = "databases/wordDatabase";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity().getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getActivity().getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    private class SearchDictionaryTask extends AsyncTask<Void, Void, Void> {

        private String[] lettersInInput;
        private View view;
        private boolean filterTextEmpty;


        public SearchDictionaryTask(String[] lettersInInput, View view, boolean filterTextEmpty){
            this.lettersInInput = lettersInInput;
            this.view = view;
            this.filterTextEmpty = filterTextEmpty;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Searching Dictionary...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            searchDictionary(lettersInInput);



            if(!filterTextEmpty){
                filterResults(lettersInInput);
            }

            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            // If there are no words in the match list, tell user it could not find any words matching those letter positions,
            // Otherwise display the matching words in the list
            if (matches.size() < 1) {
                Toast.makeText(getContext(), "No Matches Found", Toast.LENGTH_LONG).show();
            } else {
                mListener.onSearchFragmentInteraction(view, matches);
            }
        }
    }

    private void searchDictionary(String[] lettersInInput){
        // Clear the array of matching words, get the entered letters and words in the dictionary
        matches.clear();
        ArrayList<Word> wordsInDictionary = null;


        boolean containsLetter = false;

        for(int i = 0; i < lettersInInput.length; i++){
            if(!lettersInInput[i].equals("?")){
                wordsInDictionary = dictionary.getWords(lettersInInput[i], i, lettersInInput.length);
                containsLetter = true;
                break;
            }
        }

        if(!containsLetter){
            wordsInDictionary = dictionary.getWordsOfLength(lettersInInput.length);
        }

        progressDialog.setMax(wordsInDictionary.size());

        int numNonQuestionMarks = 0;
        int x = 0;

        // For each letter in the input letters, if it's not a '?', increment the number of non-question-mark letters
        // These non-question-mark letters are the equivalent of a tile on the Scrabble board
        for (String letter : lettersInInput) {
            if (!letter.equals("?")) {
                numNonQuestionMarks++;
            }
        }

        // For each word in the dictionary
        for (Word word : wordsInDictionary) {
            int numLettersMatch = 0;
            // Split the word into its individual letters
            String[] lettersInWord = word.getWord().split("");


            // If the length of the word is equal to the length of letters in the input
            if (word.getWord().length() == lettersInInput.length) {
                // For each letter
                for (int i = 0; i < lettersInWord.length; i++) {
                    String letter = lettersInWord[i];

                    // If it's not equal to an empty string
                    if (!letter.equals("")) {
                        // Get the position of that letter
                        int index = i;

                        // If the position matches the position of the letter in the input
                        if (lettersInInput[index - 1].equals(letter)) {
                            // Increment the number of matching letters
                            numLettersMatch++;
                        }
                    }
                }
            }

            // If the number of letters with matching positions is equal to the number of non-question-mark letters
            if ((numLettersMatch == numNonQuestionMarks) && (word.getWord().length() == lettersInInput.length)) {
                // Add this word to the match list
                matches.add(word);
            }

            x++;
            progressDialog.setProgress(x);
        }
    }

    private void filterResults(String[] lettersOnBoard){
        String[] lettersInRack = editTextLettersRack.getText().toString().split("-");
        ArrayList<Word> filterMatches = new ArrayList<>();
        ArrayList<Word> letterFilterMatches = new ArrayList<>();
        String[] alphabet = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Set<String> lettersNotInRack = new HashSet<String>();
        Map<String, Integer> letterCounts = new HashMap<>();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Filtering results...");
            }
        });

        int progress = 0;
        progressDialog.setProgress(progress);
        progressDialog.setMax(lettersOnBoard.length);

        for (int i = 0; i < lettersOnBoard.length; i++) {
            String letterBoard = lettersOnBoard[i];

            if (letterBoard.equals("?")) {
                for (String letterRack : lettersInRack) {
                    for (Word word : this.matches) {
                        if (word.getWord().length() == lettersOnBoard.length) {
                            if (word.getWord().indexOf(letterRack) == i) {
                                filterMatches.add(word);
                            }
                        }
                    }
                }
            }

            progress++;
            progressDialog.setProgress(progress);
        }

        for (String letter : alphabet) {
            int numMatches = 0;
            for (String letterInRack : lettersInRack) {
                if (letter.equals(letterInRack)) {
                    numMatches++;
                }
            }

            for (String letterOnBoard : lettersOnBoard) {
                if (letter.equals(letterOnBoard)) {
                    numMatches++;
                }
            }

            if (numMatches == 0) {
                lettersNotInRack.add(letter);
            }

        }

        for (String letter : lettersInRack) {
            int letterCount = this.countLetter(editTextLettersRack.getText().toString(), letter);
            letterCounts.put(letter, letterCount);
        }

        for (String letter : lettersOnBoard) {
            int letterCount = this.countLetter(editTextLettersRack.getText().toString(), letter);
            letterCounts.put(letter, letterCount);
        }

        int counter;

        progress = 0;
        progressDialog.setProgress(progress);
        progressDialog.setMax(this.matches.size());


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setIndeterminate(false);
                progressDialog.setMessage("Filtering results...");

            }
        });

        for (Word word : this.matches) {
            counter = 0;
            for (String letter : lettersNotInRack) {
                if (word.getWord().contains(letter)) {
                    counter++;
                }
            }

            if (counter == 0) {
                letterFilterMatches.add(word);
            }

            progress++;
            progressDialog.setProgress(progress);
        }

        Iterator iterator = letterFilterMatches.iterator();
        String[] lettersInWord = null;

        while (iterator.hasNext()) {
            Word nextWord = (Word) iterator.next();
            lettersInWord = nextWord.getWord().split("");

            for (String letter : lettersInWord) {
                if(!letter.equals("")) {
                    int letterCountInWord = this.countLetter(nextWord.getWord(), letter);
                    int letterCountInRack = 0;

                    if (letterCounts.containsKey(letter)) {
                        letterCountInRack = letterCounts.get(letter);
                    }

                    if (letterCountInWord > letterCountInRack) {
                        if (letterFilterMatches.contains(nextWord)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        if (checkOnlyLettersRack.isChecked()) {
            matches = letterFilterMatches;
        } else {
            matches = filterMatches;
        }
    }

    private int countLetter(String word, String letter) {
        String[] letters = word.split("");
        int counter = 0;

        for (String letterInWord : letters) {
            if (letterInWord.equals(letter)) {
                counter++;
            }
        }

        return counter;
    }



    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSearchFragmentInteraction(View view, ArrayList<Word> searchMatches);
    }

    public void updateWordProgressLabel(String text){

    }

}
