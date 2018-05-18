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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leinardi.android.speeddial.SpeedDialView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


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

    private LinkedHashMap<String, Integer> searchResults;
    private ResultListViewAdapter adapter;
    private Dictionary dictionary;
    ArrayList<String> synonyms = new ArrayList<>();
    private ListView listResults;

    LinkedHashMap<String, Integer> topWords;

    private static int listSizeLimit = 25;

    private Gson gson;

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

        gson = new Gson();

        topWords = new LinkedHashMap<>();

        Bundle bundle = getArguments();
        final Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        this.listResults = (ListView) view.findViewById(R.id.listSearchResults);
        this.searchResults = gson.fromJson(bundle.getString("Search Results"), new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());


        if(this.searchResults.size() > listSizeLimit){
            Toast.makeText(getActivity(), "Showing only the top " + listSizeLimit + " highest scoring words", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Showing all results", Toast.LENGTH_SHORT).show();
        }

        Iterator iterator = searchResults.entrySet().iterator();
        int count = 0;

        while(iterator.hasNext() && (count < listSizeLimit)){
            Map.Entry pair = (Map.Entry) iterator.next();

            topWords.put((String) pair.getKey(), (Integer) pair.getValue());
            count++;
        }

        this.adapter = new ResultListViewAdapter(getActivity(), topWords, R.layout.row_result_list);
        listResults.setAdapter(adapter);



        final SpeedDialView speedDialView = view.findViewById(R.id.speedDialCompareScores);
        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                ArrayList<String> wordsToCompare = new ArrayList<>();
                for(int i = 0; i < adapter.getCount(); i++){
                    wordsToCompare.add(adapter.getItem(i));
                }

                if(wordsToCompare.size() > 1){
                    mListener.onResultsFragmentButtonInteraction("compare", wordsToCompare);
                } else if(wordsToCompare.size() < 1){
                    Toast.makeText(getActivity(), "Please select at least two words to compare with each other", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {

            }
        });

        CheckBox checkBoxOfficialOnly = (CheckBox) view.findViewById(R.id.chkOfficial);

        checkBoxOfficialOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ArrayList<String> officialWords = new ArrayList<>();

                    for(int i = 0; i < adapter.getCount(); i++){
                        if(dictionary.isWordOfficial(adapter.getItem(i))){
                            officialWords.add(adapter.getItem(i));
                        }
                    }

                    LinkedHashMap<String, Integer> officialWordsMap = dictionary.createWordScoreMap(officialWords);

                    adapter = new ResultListViewAdapter(getActivity(), officialWordsMap, R.layout.row_result_list);
                    listResults.setAdapter(adapter);
                } else {
                    adapter = new ResultListViewAdapter(getActivity(), topWords, R.layout.row_result_list);
                    listResults.setAdapter(adapter);
                }
            }
        });

        final EditText editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);

//        editTextSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String search = editTextSearch.getText().toString();
//                ArrayList<String> results = new ArrayList<>();
//
//                for (String word : searchResults) {
//                    if (word.startsWith(search)) {
//                        results.add(word);
//                    }
//                }
//
//                adapter = new ResultListViewAdapter(getActivity(), results, R.layout.row_result_list);
//                listResults.setAdapter(adapter);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        listResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(125);

                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        Log.e("InterruptedException", e.getMessage());
                        Thread.currentThread().interrupt();
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

                        switch (which) {
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

        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String word = (String) listResults.getItemAtPosition(position);
                ArrayList<String> words = new ArrayList<>();
                words.add(word);

                mListener.onResultsFragmentButtonInteraction("details", words);
            }
        });

        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("hint", Context.MODE_PRIVATE);
        boolean shown = sharedPreferences.getBoolean("shown", false);

        if (!shown) {
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