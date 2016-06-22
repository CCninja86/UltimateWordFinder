package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;


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
    private ResultListAdapter adapter;
    private Dictionary dictionary;
    private ListView listResults;

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

        Bundle bundle = getArguments();
        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        this.listResults = (ListView) view.findViewById(R.id.listSearchResults);
        this.searchResults = bundle.getStringArrayList("Search Results");
        this.adapter = new ResultListAdapter(getActivity(), this.searchResults);
        listResults.setAdapter(adapter);

        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.toggleSelected(new Integer(position));
                adapter.notifyDataSetChanged();
            }
        });

        final ProgressBar progressOfficial = (ProgressBar) view.findViewById(R.id.progressOfficial);
        final TextView textViewProgress = (TextView) view.findViewById(R.id.textViewProgress);

        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String action = "";

                switch (view.getId()){
                    case R.id.btnOfficial:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Create two new ArrayList objects
                                //if (!thread.isInterrupted()) {
                                ArrayList<String> selectedResults = new ArrayList<>();
                                ArrayList<String> officialWords = new ArrayList<>();

                                int len = listResults.getCount();
                                SparseBooleanArray checked = listResults.getCheckedItemPositions();

                                for (int i = 0; i < len; i++) {
                                    if (checked.get(i)) {
                                        String item = listResults.getItemAtPosition(i).toString();
                                        selectedResults.add(item);
                                    }
                                }
                                // If there are words selected, add all those words to the selectedResults list, otherwise notify the user that they must select atleast one word
                                if (selectedResults.size() < 1) {
                                    Toast.makeText(getContext(), "Please select at least one word", Toast.LENGTH_LONG).show();
                                } else {
                                    // Initialise some variables
                                    int officialWordCount = 0;
                                    int wordCount = 0;
                                    boolean wordIsValid = false;
                                    progressOfficial.setProgress(0);
                                    progressOfficial.setMax(selectedResults.size());

                                    // For each word in the list of selected words
                                    for (String result : selectedResults) {
                                        float current = (float) wordCount;
                                        float total = (float) selectedResults.size();
                                        final float percentage = round((current*100)/total, 2);


                                        final String word = result;

                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textViewProgress.setText("Checking words...(" + percentage + "%)");
                                            }
                                        });

                                        // Check that it's an official Scrabble word
                                        wordIsValid = dictionary.isWordOfficial(result);

                                        if (!dictionary.database.getReadableDatabase().isOpen()) {
                                            break;
                                        } else {
                                            // If the word is an official Scrabble word, increase the number of official words and add it to a list
                                            if (wordIsValid) {
                                                officialWordCount++;
                                                officialWords.add(result);
                                            }

                                            wordCount++;
                                            final int progress = wordCount;
                                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressOfficial.setProgress(progress);
                                                }
                                            });
                                        }
                                    }

                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textViewProgress.setText("Checking Words...\nDone!");
                                        }
                                    });

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
                                            adapter = new ResultListAdapter(getActivity(), searchResults);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                }
                                            });
                                        } else if (officialWordCount == selectedResults.size()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "All of these words are official Scrabble words! The list will now change to display just the official words from your selection.", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            searchResults = officialWords;
                                            adapter = new ResultListAdapter(getActivity(), searchResults);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                }
                                            });
                                        } else if (officialWordCount == 0) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "None of these words are official Scrabble Words.", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            searchResults = officialWords;
                                            adapter = new ResultListAdapter(getActivity(), searchResults);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listResults.setAdapter(adapter);
                                                }
                                            });
                                        }
                                    }
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
                        } else {
                            Toast.makeText(getContext(), String.valueOf(dictionary.getBaseWordScore(selectedResults.get(0))), Toast.LENGTH_LONG).show();
                        }

                        break;
                    case R.id.btnGetDefinition:
                        ArrayList<String> selectedWords = new ArrayList<>();

                        int length = listResults.getCount();
                        SparseBooleanArray checkedWords = listResults.getCheckedItemPositions();

                        for (int i = 0; i < length; i++) {
                            if (checkedWords.get(i)) {
                                String item = listResults.getItemAtPosition(i).toString();
                                selectedWords.add(item);
                            }
                        }

                        if(selectedWords.size() > 1){
                            Toast.makeText(getContext(), "Please only select one word at a time for this feature.", Toast.LENGTH_LONG).show();
                        } else {
                            mListener.onResultsFragmentInteraction("definition", selectedWords);
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
                        } else {
                            Toast.makeText(getContext(), "Please select at least one word to use this feature", Toast.LENGTH_LONG).show();
                        }

                        break;
                    case R.id.btnSelectAll:
                        for(int i = 0; i < listResults.getAdapter().getCount(); i++){
                            if(listResults.isItemChecked(i) == false){
                                listResults.setItemChecked(i, true);
                                adapter.toggleSelected(new Integer(i));
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
        Button btnGetDefinition = (Button) view.findViewById(R.id.btnGetDefinition);
        Button btnCompareScores = (Button) view.findViewById(R.id.btnCompareScores);
        Button btnSelectAll = (Button) view.findViewById(R.id.btnSelectAll);
        Button btnDeselectAll = (Button) view.findViewById(R.id.btnDeselectAll);

        btnIsOfficial.setOnClickListener(onClickListener);
        btnMinScore.setOnClickListener(onClickListener);
        btnGetDefinition.setOnClickListener(onClickListener);
        btnCompareScores.setOnClickListener(onClickListener);
        btnSelectAll.setOnClickListener(onClickListener);
        btnDeselectAll.setOnClickListener(onClickListener);

        return view;
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
    }

}
