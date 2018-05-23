package com.example.james.ultimatewordfinderr;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nz.co.ninjastudios.datamuseandroid.Word;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordDetailsSynonymsTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordDetailsSynonymsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailsSynonymsTabFragment extends Fragment implements WordOptionsHandlerResultsListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView listResults;
    private LinearLayoutManager layoutManager;
    private SynonymsListViewAdapter adapter;

    private TextView textViewNumResults;
    ArrayList<Word> synonyms;

    private ProgressDialog progressDialog;

    public WordDetailsSynonymsTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordDetailsSynonymsTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordDetailsSynonymsTabFragment newInstance(String param1, String param2) {
        WordDetailsSynonymsTabFragment fragment = new WordDetailsSynonymsTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_word_details_synonyms_tab, container, false);

        Bundle bundle = getArguments();
        String word = bundle.getString("Word");
        WordOptionsHandler wordOptionsHandler = new WordOptionsHandler(this, getActivity(), word);
        wordOptionsHandler.loadSynonyms();


        listResults = view.findViewById(R.id.listViewResults);
        layoutManager = new LinearLayoutManager(getActivity());
        listResults.setLayoutManager(layoutManager);

        textViewNumResults = view.findViewById(R.id.textViewNumResults);


        /*final EditText editTextSearch = view.findViewById(R.id.editTextSearch);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = editTextSearch.getText().toString();
                ArrayList<Word> results = new ArrayList<>();

                for (Word synonym : synonyms) {
                    if (synonym.getWord().startsWith(search)) {
                        results.add(synonym);
                    }
                }

                adapter = new SynonymsListViewAdapter(getActivity(), results, R.layout.row_result_list);
                listResults.setAdapter(adapter);
                textViewNumResults.setText("Found " + listResults.getCount() + " results");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String word, ArrayList<Word> synonyms) {
        if (mListener != null) {
            mListener.onFragmentInteraction(word, synonyms);
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

    @Override
    public void onSynonymsSuccess(String word, ArrayList<Word> synonyms) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        this.synonyms = synonyms;
        adapter = new SynonymsListViewAdapter(getActivity(), synonyms);
        listResults.setAdapter(adapter);
    }

    @Override
    public void onDefinitionsSuccess(String word, DefinitionList definitionList) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String word, ArrayList<Word> synonyms);

        void onFragmentInteraction(String word, DefinitionList definitionList);

        void onFragmentInteraction(String option);
    }
}
