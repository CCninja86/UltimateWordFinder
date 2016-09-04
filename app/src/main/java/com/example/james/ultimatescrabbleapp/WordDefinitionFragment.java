package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordDefinitionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordDefinitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDefinitionFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<String> listHeader;
    HashMap<String, List<String>> listChild;
    Gson gson = new Gson();
    DefinitionList definitionList;

    private OnFragmentInteractionListener mListener;

    public WordDefinitionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordDefinitionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordDefinitionFragment newInstance(String param1, String param2) {
        WordDefinitionFragment fragment = new WordDefinitionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_word_definition, container, false);

        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        prepareListData();



        return view;
    }

    private void prepareListData(){
        listHeader = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();
        definitionList = new DefinitionList();

        // Retrieve Definitions
        Bundle bundle = getArguments();
        String word = bundle.getString("word");
        String queryURL = "https://owlbot.info/api/v1/dictionary/" + word + "?format=json";

        RetrieveDefinitionsTask task = new RetrieveDefinitionsTask(queryURL, word);
        task.execute();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        void onFragmentInteraction(Uri uri);
    }

    class RetrieveDefinitionsTask extends AsyncTask<Void, Void, Void> {

        String queryURL;
        String word;

        private RetrieveDefinitionsTask(String url, String word){
            this.queryURL = url;
            this.word = word;
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
        protected void onPostExecute(Void result){
            int i = 0;


            for(Definition definition : definitionList.getDefinitions()){
                listHeader.add("Definition " + (i + 1));

                List<String> definitionParts = new ArrayList<>();
                definitionParts.add(definition.getType());
                definitionParts.add(definition.getDefinition());
                definitionParts.add(definition.getExample());

                listChild.put(listHeader.get(i), definitionParts);

                i++;
            }

            listAdapter = new ExpandableListAdapter(getContext(), listHeader, listChild);
            listView.setAdapter(listAdapter);
        }
    }
}
