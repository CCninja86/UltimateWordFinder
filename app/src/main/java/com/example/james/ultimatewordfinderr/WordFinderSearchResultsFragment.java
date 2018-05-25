package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leinardi.android.speeddial.SpeedDialView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nz.co.ninjastudios.datamuseandroid.Word;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderSearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderSearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderSearchResultsFragment extends Fragment implements WordOptionsHandlerResultsListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener onClickListener;

    private LinkedHashMap<String, Integer> searchResults;
    private Dictionary dictionary;
    ArrayList<String> synonyms = new ArrayList<>();

    private RecyclerView listResults;
    private LinearLayoutManager layoutManager;
    private ResultListViewAdapter adapter;
    private DragSelectTouchListener dragSelectTouchListener;
    private DragSelectionProcessor dragSelectionProcessor;
    private DragSelectionProcessor.Mode mode = DragSelectionProcessor.Mode.Simple;
    private ResultListViewAdapter.ItemClickListener itemClickListener;

    LinkedHashMap<String, Integer> topWords;

    private static int listSizeLimit = 25;

    private Gson gson;
    private WordOptionsHandlerResultsListener wordOptionsHandlerResultsListener;

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
        Bundle bundle = getArguments();
        final Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();

        gson = new Gson();
        wordOptionsHandlerResultsListener = this;
        topWords = new LinkedHashMap<>();

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

        this.listResults = view.findViewById(R.id.listSearchResults);
        layoutManager = new LinearLayoutManager(getActivity());
        listResults.setLayoutManager(layoutManager);

        this.adapter = new ResultListViewAdapter(getActivity(), topWords);
        listResults.setAdapter(adapter);

        itemClickListener = new ResultListViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String word = (String) adapter.getItemAtPosition(position).getKey();
                ArrayList<String> words = new ArrayList<>();
                words.add(word);

                mListener.onResultsFragmentButtonInteraction("details", words);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                dragSelectTouchListener.startDragSelection(position);
                return true;
            }
        };

        adapter.setClickListener(itemClickListener);

        dragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public Set<Integer> getSelection() {
                return adapter.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return adapter.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                adapter.selectRange(start, end, isSelected);
            }
        }).withMode(DragSelectionProcessor.Mode.Simple);

        dragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(dragSelectionProcessor);

        listResults.addOnItemTouchListener(dragSelectTouchListener);

        final SpeedDialView speedDialView = view.findViewById(R.id.speedDialCompareScores);
        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                ArrayList<String> wordsToCompare = adapter.getSelectedWords();

                if(wordsToCompare.size() == 0){
                    for(int i = 0; i < adapter.getItemCount(); i++){
                        wordsToCompare.add((String) adapter.getItemAtPosition(i).getKey());
                    }
                }

                if(wordsToCompare.size() > 1){
                    mListener.onResultsFragmentButtonInteraction("compare", wordsToCompare);
                } else {
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

                    for(int i = 0; i < adapter.getItemCount(); i++){
                        if(dictionary.isWordOfficial((String) adapter.getItemAtPosition(i).getKey())){
                            officialWords.add((String) adapter.getItemAtPosition(i).getKey());
                        }
                    }

                    LinkedHashMap<String, Integer> officialWordsMap = dictionary.createWordScoreMap(officialWords);

                    adapter = new ResultListViewAdapter(getActivity(), officialWordsMap);
                    listResults.setAdapter(adapter);
                } else {
                    adapter = new ResultListViewAdapter(getActivity(), topWords);
                    listResults.setAdapter(adapter);
                }

                adapter.setClickListener(itemClickListener);
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
            editor.apply();
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

    @Override
    public void onSynonymsSuccess(String word, ArrayList<Word> synonyms) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        mListener.onResultsFragmentInteraction(word, synonyms);
    }

    @Override
    public void onDefinitionsSuccess(String word, DefinitionList definitionList) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        mListener.onResultsFragmentInteraction(word, definitionList);
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

        public void onResultsFragmentInteraction(String word, ArrayList<Word> synonyms);
    }

}