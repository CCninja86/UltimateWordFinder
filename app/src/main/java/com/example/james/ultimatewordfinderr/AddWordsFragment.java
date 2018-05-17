package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
public class AddWordsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    private String m_text = "";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWordsFragment.
     */

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

        editTextWords = (EditText) view.findViewById(R.id.editTextWords);
        final ListView listViewWordScores = (ListView) view.findViewById(R.id.listViewWordScores);

        editTextWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> words = new ArrayList<>();

                if (!editTextWords.getText().toString().isEmpty()) {
                    String[] enteredWords = editTextWords.getText().toString().split(",");

                    for (String enteredWord : enteredWords) {
                        words.add(enteredWord);
                    }
                }

                WordScoresListViewAdapter adapter = new WordScoresListViewAdapter(getActivity(), words, R.layout.word_scores_row);
                listViewWordScores.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Bundle bundle = getArguments();
        this.player = (Player) bundle.getSerializable("Player");


        Button addWordScoreButton = (Button) view.findViewById(R.id.btnAddWordScore);
        Button addManualScoreButton = (Button) view.findViewById(R.id.btnAddManualScore);

        addManualScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View finalView = view;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Manual Score");
                builder.setMessage("Enter the custom number");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.addCustomScore(Integer.valueOf(input.getText().toString()));
                        Toast.makeText(getActivity(), "Added score to total!", Toast.LENGTH_SHORT).show();

                        mListener.onAddWordsFragmentInteraction(finalView);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        addWordScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> words = new ArrayList<>();
                String[] enteredWords = null;

                if (!editTextWords.getText().toString().isEmpty()) {
                    enteredWords = editTextWords.getText().toString().split(",");

                    for (String enteredWord : enteredWords) {
                        words.add(enteredWord);
                    }
                }

                Map<String, Integer> wordsWithBonuses = new HashMap<>();
                ArrayList<String> doubleLetters = new ArrayList<>();
                ArrayList<String> tripleLetters = new ArrayList<>();
                boolean doubleLetter = false;
                boolean tripleLetter = false;
                boolean doubleWord = false;
                boolean tripleWord = false;


                for (int i = 0; i < listViewWordScores.getCount(); i++) {
                    RelativeLayout row = (RelativeLayout) listViewWordScores.getChildAt(i);

                    for (int x = 0; x < row.getChildCount(); x++) {
                        LinearLayout linearLayout = (LinearLayout) row.getChildAt(0);

                        for (int j = 0; j < linearLayout.getChildCount(); j++) {
                            View childView = linearLayout.getChildAt(j);

                            if (childView.getClass() == TextView.class) {
                                TextView textViewLetter = (TextView) childView;

                                String textViewText = textViewLetter.getText().toString();

                                if (textViewText.contains("x2")) {
                                    doubleLetters.add(textViewText.substring(0, textViewText.indexOf("x") - 1));
                                    doubleLetter = true;
                                } else if (textViewText.contains("x3")) {
                                    tripleLetters.add(textViewText.substring(0, textViewText.indexOf("x") - 1));
                                    tripleLetter = true;
                                }

                            } else if (childView.getClass() == Button.class) {
                                Button buttonWordBonus = (Button) childView;

                                String buttonText = buttonWordBonus.getText().toString();

                                switch (buttonText) {
                                    case "Double Word":
                                        wordsWithBonuses.put(enteredWords[i], 2);
                                        doubleWord = true;
                                        break;
                                    case "Triple Word":
                                        wordsWithBonuses.put(enteredWords[i], 3);
                                        tripleWord = true;
                                        break;
                                }
                            }
                        }
                    }

                    player.addWordScore(enteredWords[i].toUpperCase(), wordsWithBonuses, doubleLetter, tripleLetter, doubleWord, tripleWord, doubleLetters, tripleLetters);
                }


                Toast.makeText(getActivity(), "Words added for " + player.getName() + "", Toast.LENGTH_SHORT).show();
                mListener.onAddWordsFragmentInteraction(view);
            }
        });


        return view;
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
        public void onAddWordsFragmentInteraction(View view);
    }

}
