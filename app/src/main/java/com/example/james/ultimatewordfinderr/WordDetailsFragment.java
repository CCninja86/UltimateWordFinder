package com.example.james.ultimatewordfinderr;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nz.co.ninjastudios.datamuseandroid.DatamuseAndroid;
import nz.co.ninjastudios.datamuseandroid.DatamuseAndroidResultsListener;
import nz.co.ninjastudios.datamuseandroid.Word;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailsFragment extends Fragment implements DatamuseAndroidResultsListener, WordOptionsHandlerResultsListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatamuseAndroid datamuseAndroid;

    private TextView textViewWord;
    private TextView textViewPartOfSpeech;
    private TextView textViewPronunciation;

    private ProgressDialog progressDialog;

    private Map<String, String> partsOfSpeechMap;

    public WordDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordDetailsFragment newInstance(String param1, String param2) {
        WordDetailsFragment fragment = new WordDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_word_details, container, false);

        partsOfSpeechMap = new HashMap<>();
        partsOfSpeechMap.put("n", "noun");
        partsOfSpeechMap.put("v", "verb");
        partsOfSpeechMap.put("adj", "adjective");
        partsOfSpeechMap.put("adv", "adverb");
        partsOfSpeechMap.put("u", "unknown");

        datamuseAndroid = new DatamuseAndroid(true);
        datamuseAndroid.withResultsListener(this);

        Bundle bundle = getArguments();
        String word = bundle.getString("word");

        textViewWord = (TextView) view.findViewById(R.id.textViewWord);
        textViewPartOfSpeech = (TextView) view.findViewById(R.id.textViewPartOfSpeech);
        textViewPronunciation = (TextView) view.findViewById(R.id.textViewPronunciation);

        textViewPartOfSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder partsOfSpeechLegend = new AlertDialog.Builder(getActivity());
                partsOfSpeechLegend.setTitle("Parts of Speech Legend");

                StringBuilder partsOfSpeechMapStringBuilder = new StringBuilder();

                for(Map.Entry entry : partsOfSpeechMap.entrySet()){
                    partsOfSpeechMapStringBuilder.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
                }

                partsOfSpeechLegend.setMessage(partsOfSpeechMapStringBuilder.toString());

                partsOfSpeechLegend.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                partsOfSpeechLegend.show();
            }
        });

        textViewWord.setText(word);

        String url = DatamuseAndroid.getRequestUrl();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Getting word details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        datamuseAndroid.spelledLike(word).setMetadataFlags(new String[]{"p", "r"}).maxResults(1).get();

        return view;
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

    @Override
    public void onResultsSuccess(ArrayList<Word> words) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        Word word = words.get(0);

        String[] tags = word.getTags();
        String pronunciationString = "";
        String partOfSpeechString = "(";

        for (String tag : tags){
            if(tag.contains("ipa_pron")){
                pronunciationString = tag.split(":")[1];
            }

            if(!tag.contains(":")){
                partOfSpeechString += tag + ",";
            }
        }

        partOfSpeechString = partOfSpeechString.substring(0, partOfSpeechString.length() - 1) + ")";

        textViewPartOfSpeech.setText(partOfSpeechString);
        textViewPronunciation.setText(pronunciationString);
    }

    @Override
    public void onSynonymsSuccess(String word, ArrayList<Word> synonyms) {

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
