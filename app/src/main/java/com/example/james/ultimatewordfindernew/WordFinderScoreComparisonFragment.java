package com.example.james.ultimatewordfindernew;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderScoreComparisonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderScoreComparisonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderScoreComparisonFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListViewAdapter adapter;
    Dictionary dictionary;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderScoreComparisonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordFinderScoreComparisonFragment newInstance(String param1, String param2) {
        WordFinderScoreComparisonFragment fragment = new WordFinderScoreComparisonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordFinderScoreComparisonFragment() {
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
        View view = inflater.inflate(R.layout.fragment_word_finder_score_comparison, container, false);

        Button btnCompareWords = (Button) view.findViewById(R.id.btnCompareWords);
        final ListView listWords = (ListView) view.findViewById(R.id.listViewWords);
        final ListView listScoreComparison = (ListView) view.findViewById(R.id.listViewComparison);
        Globals g = Globals.getInstance();
        this.dictionary = g.getDictionary();

        Bundle bundle = getArguments();
        final ArrayList<String> wordsToCompare = bundle.getStringArrayList("wordsToCompare");

        this.adapter = new ListViewAdapter(getActivity(), wordsToCompare, R.layout.row);
        listWords.setAdapter(adapter);

        listWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.toggleSelected(new Integer(position));
                adapter.notifyDataSetChanged();
            }
        });

        btnCompareWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> words = new ArrayList<>();

                int len = listWords.getCount();
                SparseBooleanArray checked = listWords.getCheckedItemPositions();

                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        String item = listWords.getItemAtPosition(i).toString();
                        words.add(item);
                    }
                }

                if (words.size() == 0) {
                    words = wordsToCompare;
                }

                ArrayList<String> wordScoreComparisons = new ArrayList<>();

                for (String word : words) {
                    int wordBaseScore = dictionary.getBaseWordScore(word);
                    String comparisonString = word + ": " + wordBaseScore;
                    wordScoreComparisons.add(comparisonString);
                }

                ListViewAdapter resultAdapter = new ListViewAdapter(getActivity(), wordScoreComparisons, R.layout.row);
                listScoreComparison.setAdapter(resultAdapter);
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onScoreComparisonFragmentInteraction(uri);
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
        public void onScoreComparisonFragmentInteraction(Uri uri);
    }

}
