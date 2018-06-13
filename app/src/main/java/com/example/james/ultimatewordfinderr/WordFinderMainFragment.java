package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderMainFragment#newInstance} factory method toJa
 * create an instance of this fragment.
 */
public class WordFinderMainFragment extends Fragment implements PatternMatcherResultsListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private com.example.james.ultimatewordfinderr.Dictionary dictionary;
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

    private Stopwatch stopwatch;


    private boolean textFlag;

    private PatternMatcher patternMatcher;

    private View callingView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderMainFragment.
     */

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

    public void backButtonWasPressed() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_finder_main, container, false);
        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        final DatabaseHandler database = new DatabaseHandler(getActivity());

        this.patternMatcher = new PatternMatcher(this);

        this.matches = new ArrayList<Word>();
        this.selectedWords = new ArrayList<String>();
        this.advancedSearchTextFilters = new ArrayList<String>();
        this.numLetterFilter = 0;

        editTextLettersBoard = (EditText) view.findViewById(R.id.editTextLettersBoard);
        editTextLettersRack = (EditText) view.findViewById(R.id.editTextLettersRack);
        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);
        Button btnExample = (Button) view.findViewById(R.id.btnExample);

        editTextLettersRack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Word Finder Example");
                builder.setMessage("Letters on Board: f????t\n" +
                        "Letters in Rack: orebstu\n\n" +
                        "Results:\n\n" +
                        "forest\n" +
                        "forset\n" +
                        "fouett\n" +
                        "froust\n" +
                        "fustet");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                callingView = view;

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Searching Dictionary...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                String boardRegex = patternMatcher.generateBoardRegex(editTextLettersBoard.getText().toString());
                patternMatcher.getAllWordsMatchingRegex(boardRegex);
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
                        AssetManager assetManager = getActivity().getAssets();
                        database.insertAllWords(assetManager, dictionary, progressBar);
                    }
                }).start();
            }
        });

        // Dev Tools - inserting entries into database

        return view;
    }

    // This is just for potential future debugging purposes, i.e. if I need to view the version of the database in the app
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

    @Override
    public void onPatternMatcherGetAllWordsMatchingRegexTaskComplete(ArrayList<Word> matches) {
        if (editTextLettersRack.getText().toString().isEmpty()) {
            new SortByValueTask(matches).execute();
        } else {
            patternMatcher.matchWithPlayerPattern(matches, editTextLettersRack.getText().toString(), editTextLettersBoard.getText().toString());
        }

    }

    @Override
    public void onPatternMatcherMatchWithPlayerPatternTaskComplete(ArrayList<Word> matches) {
        new SortByValueTask(matches).execute();
    }

    private class SortByValueTask extends AsyncTask<Void, Void, Void> {

        LinkedHashMap<String, Integer> unsortedMap;
        LinkedHashMap<String, Integer> sortedMap;
        ArrayList<Word> matches;

        public SortByValueTask(ArrayList<Word> matches) {
            this.unsortedMap = new LinkedHashMap<>();
            this.sortedMap = new LinkedHashMap<>();
            this.matches = matches;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Processing...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.unsortedMap = dictionary.createWordScoreMap(dictionary.getStringWordList(this.matches));

            List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (sortedMap.size() > 0) {
                mListener.onSearchFragmentInteraction(callingView, sortedMap);
            } else {
                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    public interface OnFragmentInteractionListener {
        public void onSearchFragmentInteraction(View view, LinkedHashMap<String, Integer> searchMatches);
    }
}