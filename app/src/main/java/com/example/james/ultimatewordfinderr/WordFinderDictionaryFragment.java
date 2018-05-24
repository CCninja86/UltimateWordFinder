package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordFinderDictionaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordFinderDictionaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordFinderDictionaryFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Dictionary dictionary;
    DefinitionList definitionList = new DefinitionList();
    ArrayList<String> synonyms = new ArrayList<>();
    private ListViewAdapter adapter;

    private ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordFinderDictionaryFragment.
     */

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

        setRetainInstance(true);
        Globals g = Globals.getInstance();
        boolean wordOptionsHintShown = g.isWordOptionsHintShown();
        this.dictionary = g.getDictionary();

        final EditText editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
        final ListView listViewResults = (ListView) view.findViewById(R.id.listViewResults);
        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> results = dictionary.getStringWordsStartingWith(editTextSearch.getText().toString().toLowerCase());
                adapter = new ListViewAdapter(getActivity(), results, R.layout.row_result_list);
                listViewResults.setAdapter(adapter);
            }
        });

        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onDictionaryFragmentInteraction((String) listViewResults.getItemAtPosition(position));
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


    public void onButtonPressed(String word) {
        if (mListener != null) {
            mListener.onDictionaryFragmentInteraction(word);
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
        public void onDictionaryFragmentInteraction(String word);
    }

}