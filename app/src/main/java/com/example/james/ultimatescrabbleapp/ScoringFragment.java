package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoringFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoringFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Scrabble scrabbleGame;
    private ArrayAdapter<String> adapter;
    private ListView players;
    private Player player;
    private ProgressDialog progressDialog;
    private Handler handler;
    public transient Context context;

    private Globals g;
    private Dictionary dictionary;

    private static final int DICTIONARY = 1;
    private static final int WORD_FINDER = 2;
    private int selection;

    private boolean hasActiveInternetConnection;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener clickListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoringFragment newInstance(String param1, String param2) {
        ScoringFragment fragment = new ScoringFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ScoringFragment() {
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
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);

        g = Globals.getInstance();
        dictionary = g.getDictionary();

        Bundle bundle = getArguments();
        this.scrabbleGame = (Scrabble) bundle.getSerializable("Scrabble Game");

        Button showScoresButton = (Button) view.findViewById(R.id.btnShowScores);
        Button tileBreakdownButton = (Button) view.findViewById(R.id.btnTileBreakdown);
        final Button wordFinderButton = (Button) view.findViewById(R.id.btnWordFinder);

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btnWordFinder){
                    if(dictionary == null) {
                        final AlertDialog.Builder builderConfirm = new AlertDialog.Builder(getContext());
                        builderConfirm.setTitle("First-time Setup");
                        builderConfirm.setMessage("Both the Dictionary and Word Finder features utilise a very large database. " +
                                "Due to this, a first-time setup is required to use these features. This setup can take several minutes, " +
                                "depending on the speed of your device. Would you like to perform the first-time setup now?");

                        builderConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        builderConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                context = getActivity().getApplicationContext();
                                LoadFragmentTask task = new LoadFragmentTask();
                                task.execute();
                            }
                        });

                        builderConfirm.show();
                    } else {
                        context = getActivity().getApplicationContext();
                        LoadFragmentTask task = new LoadFragmentTask();
                        task.execute();
                    }
                } else {
                    mListener.onScoringFragmentButtonInteraction(view);
                }


            }
        };



        showScoresButton.setOnClickListener(clickListener);
        tileBreakdownButton.setOnClickListener(clickListener);
        wordFinderButton.setOnClickListener(clickListener);

        players = (ListView) view.findViewById(android.R.id.list);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, scrabbleGame.getPlayerNames());
        players.setAdapter(adapter);
        players.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String playerName = players.getItemAtPosition(position).toString();
                Player player = scrabbleGame.getPlayerByName(playerName);
                mListener.onScoringFragmentListInteraction(player, scrabbleGame);
            }
        });

        return view;
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
        public void onScoringFragmentListInteraction(Player player, Scrabble scrabbleGame);
        public void onScoringFragmentButtonInteraction(View view);
    }

    private class LoadFragmentTask extends AsyncTask<Void, Void, Void> {





        public LoadFragmentTask(){

        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Performing first-time setup...");
            progressDialog.setMessage("Loading Dictionary...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(dictionary == null){
                dictionary = new com.example.james.ultimatescrabbleapp.Dictionary();
                final DatabaseHandler database = new DatabaseHandler(context);
                dictionary.linkDatabase(database);
                dictionary.setWordList();
                g.setDictionary(dictionary);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("Checking Internet Connection...");
                }
            });

            hasActiveInternetConnection = hasActiveInternetConnection();

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            choose();
        }
    }

    private void choose(){
        String alertMessage = "Would you like to open the Word Finder or Basic Dictionary?";

        if(!hasActiveInternetConnection){
            alertMessage += "\n\nNote: You are not connected to the Internet. You will not be able to view the definitions of words. All other features will be available.";
        }

        AlertDialog.Builder builderSelection = new AlertDialog.Builder(getContext());
        builderSelection.setTitle("Word Finder or Dictionary?");
        builderSelection.setMessage(alertMessage);

        builderSelection.setNegativeButton("Dictionary", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selection = DICTIONARY;
                setup();
            }
        });

        builderSelection.setPositiveButton("Word Finder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selection = WORD_FINDER;
                setup();
            }
        });

        builderSelection.show();
    }

    private void setup(){
        Bundle bundle = new Bundle();
        bundle.putInt("selection", selection);
        Intent intent = new Intent(context, WordFinderActivity.class);
        intent.putExtra("selection", bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private boolean hasActiveInternetConnection(){
        boolean success = false;

        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }
}




