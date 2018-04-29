package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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
public class WordFinderSearchResultsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener onClickListener;

    private ArrayList<String> searchResults;
    private ListViewAdapter adapter;
    private Dictionary dictionary;
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
        setRetainInstance(true);

        final TextView textViewNumResults = (TextView) view.findViewById(R.id.textViewNumResults);

        Bundle bundle = getArguments();
        final Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        this.listResults = (ListView) view.findViewById(R.id.listSearchResults);
        this.searchResults = bundle.getStringArrayList("Search Results");
        this.adapter = new ListViewAdapter(getActivity(), this.searchResults, R.layout.row);
        listResults.setAdapter(adapter);
        textViewNumResults.setText("Found " + listResults.getCount() + " results");

        final Switch switchSmartSelection = (Switch) view.findViewById(R.id.switchSmartSelection);
        final Switch switchUseOfficialSelection = (Switch) view.findViewById(R.id.switchOfficialSelection);
        switchSmartSelection.setChecked(true);

        final EditText editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = editTextSearch.getText().toString();
                ArrayList<String> results = new ArrayList<>();

                for(String word : searchResults){
                    if(word.startsWith(search)){
                        results.add(word);
                    }
                }

                adapter = new ListViewAdapter(getActivity(), results, R.layout.row);
                listResults.setAdapter(adapter);
                textViewNumResults.setText("Found " + listResults.getCount() + " results");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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


                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                if(vibrator.hasVibrator()){
                    vibrator.vibrate(125);

                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final CharSequence options[] = new CharSequence[]{"Definitions", "Synonyms"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        synonyms.clear();
                        String word = listResults.getItemAtPosition(position).toString();
                        WordOptionsHandler wordOptionsHandler = new WordOptionsHandler(null, mListener, null, getActivity(), word);

                        switch(which){
                            case 0:
                                wordOptionsHandler.loadDefinitions();
                                break;
                            case 1:
                                wordOptionsHandler.loadSynonyms();
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
                        progressDialog = new ProgressDialog(getActivity());
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
                                            Toast.makeText(getActivity(), "Please select at least one word", Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(getActivity(), "Some of these words are official Scrabble words! The list will now change to display just the official words from your selection.", Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(getActivity(), "All of these words are official Scrabble words! The list will now change to display just the official words from your selection.", Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(getActivity(), "None of these words are official Scrabble Words.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getActivity(), "Please only select one word at a time for this feature.", Toast.LENGTH_LONG).show();
                        } else if(selectedResults.size() == 1){
                            Toast.makeText(getActivity(), String.valueOf(dictionary.getBaseWordScore(selectedResults.get(0))), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Please select at least one word", Toast.LENGTH_LONG).show();
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

                            mListener.onResultsFragmentButtonInteraction("compare", wordsToCompare);
                        } else if(wordsToCompare.size() == 1) {
                            Toast.makeText(getActivity(), "Please select at least one word to use this feature", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Please select at least one word", Toast.LENGTH_LONG).show();
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

                                Toast.makeText(getActivity(), "More than 100 items in list, selecting all currently visible items...", Toast.LENGTH_LONG).show();
                            } else {
                                for(int i = 0; i < listSize; i++){
                                    if(listResults.isItemChecked(i) == false){
                                        listResults.setItemChecked(i, true);
                                        adapter.toggleSelected(new Integer(i));
                                    }
                                }

                                Toast.makeText(getActivity(), "100 or less items in list, selecting all items in list...", Toast.LENGTH_LONG).show();
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

        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("hint", Context.MODE_PRIVATE);
        boolean shown = sharedPreferences.getBoolean("shown", false);

        if(!shown){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("You can view the definitions and synonyms for the majority of words by long-pressing any word in the list, which will pop-up a list of options for that word");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
            });

            builder.show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("shown", true);
            editor.commit();
        }


        return view;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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
        public void onResultsFragmentButtonInteraction(String action, ArrayList<String> selectedWords);
        public void onResultsFragmentInteraction(String word, DefinitionList definitionList);
        public void onResultsFragmentInteraction(String word, ArrayList<String> synonyms);
    }

}