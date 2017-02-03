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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * {@link WordFinderDictionaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderDictionaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderDictionaryFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Dictionary dictionary;
    DefinitionList definitionList = new DefinitionList();
    ArrayList<String> synonyms = new ArrayList<>();
    private ListViewAdapter adapter;

    ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderDictionaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordFinderDictionaryFragment newInstance(String param1, String param2) {
        WordFinderDictionaryFragment fragment = new WordFinderDictionaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordFinderDictionaryFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_finder_dictionary, container, false);

        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();

        final EditText editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
        final ListView listViewResults = (ListView) view.findViewById(R.id.listViewResults);
        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> results = dictionary.getStringWordsStartingWith(editTextSearch.getText().toString());
                adapter = new ListViewAdapter(getActivity(), results, R.layout.row);
                listViewResults.setAdapter(adapter);
            }
        });

        listViewResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        String word = listViewResults.getItemAtPosition(position).toString();

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
                mListener.onDictionaryFragmentInteraction(synonyms);
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
                mListener.onDictionaryFragmentInteraction(definitionList);
            }

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(DefinitionList definitionList) {
        if (mListener != null) {
            mListener.onDictionaryFragmentInteraction(definitionList);
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
        public void onDictionaryFragmentInteraction(DefinitionList definitionList);
        public void onDictionaryFragmentInteraction(ArrayList<String> synonyms);
    }

}
