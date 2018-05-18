package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jamesfra.datamuseandroid.DatamuseAndroid;
import com.example.jamesfra.datamuseandroid.DatamuseAndroidResultsListener;
import com.example.jamesfra.datamuseandroid.Word;

import junit.framework.Test;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailsFragment extends Fragment implements DatamuseAndroidResultsListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatamuseAndroid datamuseAndroid;
    private DatamuseAndroidResultsListener datamuseAndroidResultsListener;

    private TextView textViewWord;
    private TextView textViewPronunciation;

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

        datamuseAndroid = new DatamuseAndroid(true);
        datamuseAndroid.withResultsListener(this);

        Bundle bundle = getArguments();
        String word = bundle.getString("word");

        textViewWord = (TextView) view.findViewById(R.id.textViewWord);
        textViewPronunciation = (TextView) view.findViewById(R.id.textViewPronunciation);

        textViewWord.setText(word);

        String url = DatamuseAndroid.getRequestUrl();

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
        Word word = words.get(0);

        String[] tags = word.getTags();
        String pronunciationString = "";


        for (String tag : tags){
            if(tag.contains("pron")){
                pronunciationString = tag.split(":")[1];
            }
        }

        textViewPronunciation.setText(pronunciationString);
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
