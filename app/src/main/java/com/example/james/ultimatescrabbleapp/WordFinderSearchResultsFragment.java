package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Vibrator;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderSearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderSearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderSearchResultsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener onClickListener;

    private ArrayList<String> searchResults;
    private ListViewAdapter adapter;
    private Dictionary dictionary;
    DefinitionList definitionList = new DefinitionList();
    ArrayList<String> synonyms = new ArrayList<>();
    private ListView listResults;

    private ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderSearchResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordFinderSearchResultsFragment newInstance(String param1, String param2) {
        WordFinderSearchResultsFragment fragment = new WordFinderSearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordFinderSearchResultsFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_finder_search_results, container, false);

        final TextView textViewNumResults = (TextView) view.findViewById(R.id.textViewNumResults);

        Bundle bundle = getArguments();
        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        this.listResults = (ListView) view.findViewById(R.id.listSearchResults);
        this.searchResults = bundle.getStringArrayList("Search Results");
        this.adapter = new ListViewAdapter(getActivity(), this.searchResults, R.layout.row);
        listResults.setAdapter(adapter);
        textViewNumResults.setText("Found " + listResults.getCount() + " results");

        final Switch switchSmartSelection = (Switch) view.findViewById(R.id.switchSmartSelection);
        switchSmartSelection.setChecked(true);

        final Switch switchUseOfficialSelection = (Switch) view.findViewById(R.id.switchOfficialSelection);



        switchUseOfficialSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    switchUseOfficialSelection.setText("Use entire list");
                } else {
                    switchUseOfficialSelection.setText("Use your selection");
                }
            }
        });

        switchSmartSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Are you sure?");
                    alertDialog.setMessage("Disabling Smart Selection will allow you to select the entire list when clicking 'Select All'." +
                            "WARNING: Trying to 'Select All' with a large list could cause the app to hang/crash. Are you sure you want to do this?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switchSmartSelection.setChecked(false);
                        }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchSmartSelection.setChecked(true);
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                adapter.toggleSelected(new Integer(position));
                adapter.notifyDataSetChanged();
            }
        });

        listResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

                if(vibrator.hasVibrator()){
                    vibrator.vibrate(250);

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final CharSequence options[] = new CharSequence[]{"Definitions", "Synonyms"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        definitionList.clearList();
                        synonyms.clear();
                        String word = listResults.getItemAtPosition(position).toString();

                        switch(which){
                            case 0:
                                GetResultsTask getResultsTaskDefinitions = new GetResultsTask(word, "Definitions");
                                getResultsTaskDefinitions.execute();
                                break;
                            case 1:
                                GetResultsTask getResultsTaskSynonyms = new GetResultsTask(word, "Synonyms");
                                getResultsTaskSynonyms.execute();
                                break;
                        }


                    }
                });

                builder.show();





                return true;
            }
        });




        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String action = "";

                switch (view.getId()){
                    case R.id.btnOfficial:
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Checking Official Words...");
                        progressDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Create two new ArrayList objects
                                //if (!thread.isInterrupted()) {
                                ArrayList<String> selectedResults = new ArrayList<>();
                                ArrayList<String> officialWords = new ArrayList<>();

                                if(switchUseOfficialSelection.isChecked()){
                                    int len = listResults.getCount();
                                    SparseBooleanArray checked = listResults.getCheckedItemPositions();

                                    for (int i = 0; i < len; i++) {
                                        if (checked.get(i)) {
                                            String item = listResults.getItemAtPosition(i).toString();
                                            selectedResults.add(item);
                                        }
                                    }
                                } else {
                                    for(int i = 0; i < listResults.getCount(); i++){
                                        String item = listResults.getItemAtPosition(i).toString();
                                        selectedResults.add(item);
                                    }
                                }

                                // If there are words selected, add all those words to the selectedResults list, otherwise notify the user that they must select atleast one word
                                if (selectedResults.size() < 1) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Please select at least one word", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    // Initialise some variables
                                    int officialWordCount = 0;
                                    int wordCount = 0;
                                    boolean wordIsValid = false;

                                    // For each word in the list of selected words
                                    for (String result : selectedResults) {
                                        float current = (float) wordCount;
                                        float total = (float) selectedResults.size();
                                        final float percentage = round((current*100)/total, 2);


                                        final String word = result;

                                        // Check that it's an official Scrabble word
                                        wordIsValid = dictionary.isWordOfficial(result);


                                        // If the word is an official Scrabble word, increase the number of official words and add it to a list
                                        if (wordIsValid) {
                                            officialWordCount++;
                                            officialWords.add(result);
                                        }

                                        wordCount++;

                                    }

                                    // If the number of selected words is greater than 0
                                    if (selectedResults.size() > 0) {
                                        // Based on the number of official Scrabble words found from the selection, display a certain message to the user,
                                        // then change the list to display just the official words
                                        if (officialWordCount > 0 && officialWordCount < selectedResults.size()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "Some of these words are official Scrabble words! The list will now change to display just the official words from your selection.", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            searchResults = officialWords;
                                            adapter = new ListViewAdapter(getActivity(), searchResults, R.layout.row);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                    textViewNumResults.setText("Found " + listResults.getCount() + " results");
                                                }
                                            });
                                        } else if (officialWordCount == selectedResults.size()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "All of these words are official Scrabble words! The list will now change to display just the official words from your selection.", Toast.LENGTH_LONG).show();
                                                    textViewNumResults.setText("Found " + listResults.getCount() + " results");
                                                }
                                            });

                                            searchResults = officialWords;
                                            adapter = new ListViewAdapter(getActivity(), searchResults, R.layout.row);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                    textViewNumResults.setText("Found " + listResults.getCount() + " results");
                                                }
                                            });
                                        } else if (officialWordCount == 0) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "None of these words are official Scrabble Words.", Toast.LENGTH_LONG).show();
                                                    textViewNumResults.setText("Found " + listResults.getCount() + " results");
                                                }
                                            });

                                            searchResults = officialWords;
                                            adapter = new ListViewAdapter(getActivity(), searchResults, R.layout.row);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                    textViewNumResults.setText("Found " + listResults.getCount() + " results");
                                                }
                                            });
                                        }
                                    }
                                }

                                if(progressDialog != null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                        }).start();

                        break;
                    case R.id.btnMinWordScore:
                        ArrayList<String> selectedResults = new ArrayList<String>();

                        int len = listResults.getCount();
                        SparseBooleanArray checked = listResults.getCheckedItemPositions();

                        for (int i = 0; i < len; i++) {
                            if (checked.get(i)) {
                                String item = listResults.getItemAtPosition(i).toString();
                                selectedResults.add(item);
                            }
                        }

                        if(selectedResults.size() > 1){
                            Toast.makeText(getContext(), "Please only select one word at a time for this feature.", Toast.LENGTH_LONG).show();
                        } else if(selectedResults.size() == 1){
                            Toast.makeText(getContext(), String.valueOf(dictionary.getBaseWordScore(selectedResults.get(0))), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Please select at least one word", Toast.LENGTH_LONG).show();
                        }

                        break;
                    case R.id.btnCompareScores:
                        ArrayList<String> wordsToCompare = new ArrayList<>();

                        int listLength = listResults.getCount();
                        SparseBooleanArray wordsChecked = listResults.getCheckedItemPositions();

                        for(int i = 0; i < listLength; i++){
                            if(wordsChecked.get(i)){
                                String word = listResults.getItemAtPosition(i).toString();
                                wordsToCompare.add(word);
                            }
                        }

                        if(wordsToCompare.size() >= 1){
                            for(int i = 0; i < listResults.getAdapter().getCount(); i++){
                                listResults.setItemChecked(i, false);
                            }

                            adapter.notifyDataSetChanged();

                            mListener.onResultsFragmentInteraction("compare", wordsToCompare);
                        } else if(wordsToCompare.size() == 1) {
                            Toast.makeText(getContext(), "Please select at least one word to use this feature", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Please select at least one word", Toast.LENGTH_LONG).show();
                        }

                        break;
                    case R.id.btnSelectAll:
                        int listSize = listResults.getAdapter().getCount();


                        if(switchSmartSelection.isChecked()){
                            if(listSize > 100){
                                int firstVisible = listResults.getFirstVisiblePosition();
                                int lastVisible = listResults.getLastVisiblePosition();

                                for(int i = firstVisible; i < lastVisible; i++){
                                    if(listResults.isItemChecked(i) == false){
                                        listResults.setItemChecked(i, true);
                                        adapter.toggleSelected(new Integer(i));
                                    }
                                }

                                Toast.makeText(getContext(), "More than 100 items in list, selecting all currently visible items...", Toast.LENGTH_LONG).show();
                            } else {
                                for(int i = 0; i < listSize; i++){
                                    if(listResults.isItemChecked(i) == false){
                                        listResults.setItemChecked(i, true);
                                        adapter.toggleSelected(new Integer(i));
                                    }
                                }

                                Toast.makeText(getContext(), "100 or less items in list, selecting all items in list...", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            for(int i = 0; i < listSize; i++){
                                if(listResults.isItemChecked(i) == false){
                                    listResults.setItemChecked(i, true);
                                    adapter.toggleSelected(new Integer(i));
                                }
                            }
                        }



                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.btnDeselectAll:
                        for(int i = 0; i < listResults.getAdapter().getCount(); i++){
                            if(listResults.isItemChecked(i) == true){
                                listResults.setItemChecked(i, false);
                                adapter.toggleSelected(new Integer(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        Button btnIsOfficial = (Button) view.findViewById(R.id.btnOfficial);
        Button btnMinScore = (Button) view.findViewById(R.id.btnMinWordScore);
        Button btnCompareScores = (Button) view.findViewById(R.id.btnCompareScores);
        Button btnSelectAll = (Button) view.findViewById(R.id.btnSelectAll);
        Button btnDeselectAll = (Button) view.findViewById(R.id.btnDeselectAll);

        btnIsOfficial.setOnClickListener(onClickListener);
        btnMinScore.setOnClickListener(onClickListener);
        btnCompareScores.setOnClickListener(onClickListener);
        btnSelectAll.setOnClickListener(onClickListener);
        btnDeselectAll.setOnClickListener(onClickListener);

        return view;
    }

    private void executeAsyncTask(AsyncTask task){
        task.execute();
    }

    private class GetResultsTask extends AsyncTask<Void, Void, Void>{

        String word;
        String resultType;


        public GetResultsTask(String word, String resultType){
            this.word = word;
            this.resultType = resultType;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Searching...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(this.resultType.equals("Definitions")){
                // Retrieve Definitions
                String queryURL = "https://owlbot.info/api/v1/dictionary/" + word + "?format=json";

                RetrieveDefinitionsTask task = new RetrieveDefinitionsTask(queryURL, word);
                executeAsyncTask(task);
            } else if(this.resultType.equals("Synonyms")){
                RetrieveSynonymsTask task = new RetrieveSynonymsTask(word);
                executeAsyncTask(task);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }

    private class RetrieveSynonymsTask extends AsyncTask<Object, Void, Void>{

        private String word;

        public RetrieveSynonymsTask(String word){
            this.word = word;
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                if(word.contains(" ")){
                    word = word.toLowerCase().replaceAll(" ", "%20");
                }

                URL url = new URL("http://www.thesaurus.com/browse/" + word);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Vodafone Smart ultra 6"
                        + " Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91"
                        + " Mobile Safari/537.36");

                if(connection.getResponseCode() == 404){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "I could not find that word", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    ArrayList<String> entries = new ArrayList<>();

                    while((line = bufferedReader.readLine()) != null){
                        System.out.println(line);
                        if(line.contains("class=\"result synstart\"")){
                            String[] list = line.split("<b>Synonyms:</b>");


                            for(int i = 0; i < list.length; i++){
                                if(i > 0){
                                    String entry = list[i];
                                    String newEntry = entry.trim().substring(0, entry.indexOf("</div>") - 1);
                                    entries.add(newEntry);
                                }
                            }
                        }
                    }

                    for(String entry : entries){
                        String[] synonymList = entry.split(", ");

                        for(String synonym : synonymList){
                            synonyms.add(synonym);
                        }
                    }

                    // Remove any potential duplicate entries from ArrayList
                    Set<String> hashSet = new HashSet<>();
                    hashSet.addAll(synonyms);
                    synonyms.clear();
                    synonyms.addAll(hashSet);
                    hashSet = null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(!synonyms.isEmpty()){
                mListener.onResultsFragmentInteraction(synonyms);
            }

        }
    }

    private class RetrieveDefinitionsTask extends AsyncTask<Object, Void, Void> {

        String queryURL;
        String word;

        private RetrieveDefinitionsTask(String url, String word){
            this.queryURL = url;
            this.word = word;
        }

        @Override
        protected Void doInBackground(Object... params) {
            URL searchUrl;

            try {
                searchUrl = new URL(queryURL);
                HttpsURLConnection connection = (HttpsURLConnection) searchUrl.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while((line = in.readLine()) != null){
                    line = line.substring(1, line.length() - 1);
                    String[] definitions = line.split(",\\{");

                    for(String definition: definitions){
                        if(!definition.startsWith("{")){
                            definition = "{" + definition;
                        }

                        Definition def = null;

                        if(definition.contains("}")){
                            Gson gson = new Gson();
                            def = gson.fromJson(definition, Definition.class);
                            definitionList.addDefinition(def);
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Oops!");
                                    builder.setMessage("I was unable to find a definition for that word. Would you like to search Google for the definition?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String queryURL = "https://www.google.co.nz/#q=" + word + "+definition";
                                            final Intent browserActivity = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(queryURL));
                                            startActivity(browserActivity);
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                }
                            });
                        }


                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(definitionList.getDefinitions().size() > 0){
                mListener.onResultsFragmentInteraction(definitionList);
            }

        }
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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
        public void onResultsFragmentInteraction(String action, ArrayList<String> selectedWords);
        public void onResultsFragmentInteraction(DefinitionList definitionList);
        public void onResultsFragmentInteraction(ArrayList<String> synonyms);
    }

}
