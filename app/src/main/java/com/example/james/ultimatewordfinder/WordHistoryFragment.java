package com.example.james.ultimatewordfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordHistoryFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Player player;
    private ArrayAdapter<String> adapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordHistoryFragment newInstance(String param1, String param2) {
        WordHistoryFragment fragment = new WordHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordHistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_word_history, container, false);

        Bundle bundle = getArguments();
        this.player = (Player) bundle.getSerializable("Player");
        final ListView listWordHistory = (ListView) view.findViewById(R.id.listWordHistory);
        this.adapter = new ListViewAdapter(getActivity(),this.player.getPlayerWordHistory(), R.layout.row);
        listWordHistory.setAdapter(this.adapter);

        listWordHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String word = listWordHistory.getItemAtPosition(position).toString();

                if(word.contains("?")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Set Blank Tile");
                    builder.setMessage("Would you like to set the blank tiles for this word to make it easier to remember the full word?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                            builder.setMessage("Enter the blank tiles in order, separated by a comma (no spaces).");

                            final EditText input = new EditText(getActivity());

                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

                            builder.setPositiveButton("Set Blanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!input.getText().toString().isEmpty()){
                                        String text = input.getText().toString();

                                        String[] blankTiles = text.split(",");

                                        int counter = 0;

                                        String[] wordCharacters = word.split("");
                                        ArrayList<String> newWordCharacters = new ArrayList<>();

                                        for(int i = 0; i < wordCharacters.length; i++){
                                            String character = wordCharacters[i];

                                            if(character.equals("?")){
                                                newWordCharacters.add(blankTiles[counter].toUpperCase());
                                                counter++;
                                            } else {
                                                newWordCharacters.add(character);
                                            }
                                        }

                                        String newWord = "";

                                        for(String character : newWordCharacters){
                                            newWord += character;
                                        }

                                        player.updatePlayerWordHistory(word, newWord);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Entry updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Please enter at least one letter", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
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
        public void onFragmentInteraction(Uri uri);
    }

}
