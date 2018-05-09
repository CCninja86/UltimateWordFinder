package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordDefinitionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordDefinitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: Change colour of Expandable ListView, as currently it does not conform to the App Theme
public class WordDefinitionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<String> listHeader;
    HashMap<String, List<String>> listChild;

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

        Bundle bundle = getArguments();
        String word = bundle.getString("Word");
        TextView textViewWord = view.findViewById(R.id.textViewWord);
        textViewWord.setText(word.toUpperCase());

        listView = view.findViewById(R.id.expandableListView);
        prepareListData();


        return view;
    }

    private void prepareListData() {
        listHeader = new ArrayList<>();
        listChild = new HashMap<>();

        Bundle bundle = getArguments();
        String json = bundle.getString("Definition List");
        DefinitionList definitionList = new Gson().fromJson(json, DefinitionList.class);


        int i = 0;


        for (Definition definition : definitionList.getDefinitions()) {
            listHeader.add("Definition " + (i + 1));

            List<String> definitionParts = new ArrayList<>();
            definitionParts.add(definition.getSubject());
            definitionParts.add(definition.getDefinition());

            listChild.put(listHeader.get(i), definitionParts);

            i++;
        }

        listAdapter = new ExpandableListAdapter(getActivity(), listHeader, listChild);
        listView.setAdapter(listAdapter);

    }


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
        void onFragmentInteraction(Uri uri);
    }

}
