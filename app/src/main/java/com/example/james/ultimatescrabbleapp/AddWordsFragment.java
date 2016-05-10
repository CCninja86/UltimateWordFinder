package com.example.james.ultimatescrabbleapp;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddWordsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddWordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWordsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener clickListener;

    private TextView textViewDoubleLetter;
    private TextView textViewTripleLetter;
    private TextView textViewDoubleWord;
    private TextView textViewTripleWord;
    private EditText editTextDoubleLetter;
    private EditText editTextTripleLetter;
    private EditText editTextDoubleWord;
    private EditText editTextTripleWord;
    private EditText editTextWords;

    private Player player;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddWordsFragment newInstance(String param1, String param2) {
        AddWordsFragment fragment = new AddWordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddWordsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_words, container, false);

        textViewDoubleLetter = (TextView) view.findViewById(R.id.textViewDoubleLetter);
        textViewTripleLetter = (TextView) view.findViewById(R.id.textViewTripleLetter);
        textViewDoubleWord = (TextView) view.findViewById(R.id.textViewDoubleWord);
        textViewTripleWord = (TextView) view.findViewById(R.id.textViewTripleWord);
        editTextDoubleLetter = (EditText) view.findViewById(R.id.editTextDoubleLetter);
        editTextTripleLetter = (EditText) view.findViewById(R.id.editTextTripleLetter);
        editTextDoubleWord = (EditText) view.findViewById(R.id.editTextDoubleWord);
        editTextTripleWord = (EditText) view.findViewById(R.id.editTextTripleWord);
        editTextWords = (EditText) view.findViewById(R.id.editTextWords);

        textViewDoubleLetter.setVisibility(View.INVISIBLE);
        textViewTripleLetter.setVisibility(View.INVISIBLE);
        textViewDoubleWord.setVisibility(View.INVISIBLE);
        textViewTripleWord.setVisibility(View.INVISIBLE);
        editTextDoubleLetter.setVisibility(View.INVISIBLE);
        editTextTripleLetter.setVisibility(View.INVISIBLE);
        editTextDoubleWord.setVisibility(View.INVISIBLE);
        editTextTripleWord.setVisibility(View.INVISIBLE);


        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox selectedCheckBox = null;

                switch (view.getId()) {
                    case R.id.checkDoubleLetter:
                        selectedCheckBox = (CheckBox) view.findViewById(R.id.checkDoubleLetter);

                        if (selectedCheckBox.isChecked()) {
                            textViewDoubleLetter.setVisibility(View.VISIBLE);
                            editTextDoubleLetter.setVisibility(View.VISIBLE);
                        } else {
                            textViewDoubleLetter.setVisibility(View.INVISIBLE);
                            editTextDoubleLetter.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case R.id.checkTripleLetter:
                        selectedCheckBox = (CheckBox) view.findViewById(R.id.checkTripleLetter);

                        if (selectedCheckBox.isChecked()) {
                            textViewTripleLetter.setVisibility(View.VISIBLE);
                            editTextTripleLetter.setVisibility(View.VISIBLE);
                        } else {
                            textViewTripleLetter.setVisibility(View.INVISIBLE);
                            editTextTripleLetter.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case R.id.checkDoubleWord:
                        selectedCheckBox = (CheckBox) view.findViewById(R.id.checkDoubleWord);

                        if (selectedCheckBox.isChecked()) {
                            textViewDoubleWord.setVisibility(View.VISIBLE);
                            editTextDoubleWord.setVisibility(View.VISIBLE);
                        } else {
                            textViewDoubleWord.setVisibility(View.INVISIBLE);
                            editTextDoubleWord.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case R.id.checkTripleWord:
                        selectedCheckBox = (CheckBox) view.findViewById(R.id.checkTripleWord);

                        if (selectedCheckBox.isChecked()) {
                            textViewTripleWord.setVisibility(View.VISIBLE);
                            editTextTripleWord.setVisibility(View.VISIBLE);
                        } else {
                            textViewTripleWord.setVisibility(View.INVISIBLE);
                            editTextTripleWord.setVisibility(View.INVISIBLE);
                        }

                        break;
                }
            }
        };

        final CheckBox checkDoubleLetter = (CheckBox) view.findViewById(R.id.checkDoubleLetter);
        final CheckBox checkTripleLetter = (CheckBox) view.findViewById(R.id.checkTripleLetter);
        final CheckBox checkDoubleWord = (CheckBox) view.findViewById(R.id.checkDoubleWord);
        final CheckBox checkTripleWord = (CheckBox) view.findViewById(R.id.checkTripleWord);


        checkDoubleLetter.setOnClickListener(clickListener);
        checkTripleLetter.setOnClickListener(clickListener);
        checkDoubleWord.setOnClickListener(clickListener);
        checkTripleWord.setOnClickListener(clickListener);

        Bundle bundle = getArguments();
        this.player = (Player) bundle.getSerializable("Player");


        Button addWordsButton = (Button) view.findViewById(R.id.btnAddWords);
        addWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> words = new ArrayList<String>();

                if(editTextWords.getText().toString().contains(",")){
                    String[] enteredWords = editTextWords.getText().toString().split(",");

                    for(String enteredWord : enteredWords){
                        words.add(enteredWord);
                    }
                } else {
                    words.add(editTextWords.getText().toString());
                }

                ArrayList<String> doubleLetters = new ArrayList<String>();
                ArrayList<String> tripleLetters = new ArrayList<String>();
                Map<String, Integer> wordsWithBonuses = new HashMap<String, Integer>();
                boolean doubleLetter = false;
                boolean tripleLetter = false;
                boolean doubleWord = false;
                boolean tripleWord = false;

                if(!editTextDoubleWord.getText().toString().isEmpty()){
                    String[] doubleWords = editTextDoubleWord.getText().toString().split(",");

                    for(String word : doubleWords){
                        wordsWithBonuses.put(word, 2);
                    }
                }

                if(!editTextTripleWord.getText().toString().isEmpty()){
                    String[] tripleWords = editTextTripleWord.getText().toString().split(",");

                    for(String word : tripleWords){
                        wordsWithBonuses.put(word, 3);
                    }
                }

                if(!editTextDoubleLetter.getText().toString().isEmpty()){
                    String[] letters = editTextDoubleLetter.getText().toString().split(",");

                    for(String letter : letters){
                        doubleLetters.add(letter.toUpperCase());
                    }
                }

                if(!editTextTripleLetter.getText().toString().isEmpty()){
                    String[] letters = editTextTripleLetter.getText().toString().split(",");

                    for(String letter : letters){
                        tripleLetters.add(letter.toUpperCase());
                    }
                }

                if(checkDoubleLetter.isChecked()){
                    doubleLetter = true;
                }

                if(checkTripleLetter.isChecked()){
                    tripleLetter = true;
                }

                if(checkDoubleWord.isChecked()){
                    doubleWord = true;
                }

                if(checkTripleWord.isChecked()){
                    tripleWord = true;
                }

                for(String playedWord : words){
                    player.addWordScore(playedWord.toUpperCase(), wordsWithBonuses, doubleLetter, tripleLetter, doubleWord, tripleWord, doubleLetters, tripleLetters);
                }

                Toast.makeText(getContext(), "Words added for " + player.getName() + "", Toast.LENGTH_LONG).show();
                mListener.onAddWordsFragmentInteraction(view);
            }
        });



        return view;
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
        public void onAddWordsFragmentInteraction(View view);
    }

}
