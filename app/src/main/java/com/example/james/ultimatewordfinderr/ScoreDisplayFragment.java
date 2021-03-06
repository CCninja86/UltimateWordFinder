package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoreDisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoreDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreDisplayFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Scrabble scrabbleGame;
    private ArrayAdapter<String> adapter;

    private Globals g;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoreDisplayFragment.
     */

    public static ScoreDisplayFragment newInstance(String param1, String param2) {
        ScoreDisplayFragment fragment = new ScoreDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ScoreDisplayFragment() {
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
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

//        Gson gson = new Gson();
//        String players = gson.toJson(scrabbleGame.getPlayers());
//        savedState.putString("Players", players);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score_display, container, false);

        g = Globals.getInstance();

        //Bundle bundle = getArguments();
        this.scrabbleGame = g.getGame();
//        Map<String, Player> playerMap = this.scrabbleGame.getPlayerMap();
//
//        for(String key : playerMap.keySet()){
//            Player player = playerMap.get(key);
//            this.scrabbleGame.getPlayerByName(key).setScore(player.getScore());
//            this.scrabbleGame.getPlayerByName(key).setPlayerWordHistory(player.getPlayerWordHistory());
//        }

        ArrayList<String> playerOverallScores = new ArrayList<String>();

        for (Player player : this.scrabbleGame.getPlayers()) {
            String playerScoreSummary = "";
            playerScoreSummary += player.getName() + ": " + player.getScore() + "";
            playerOverallScores.add(playerScoreSummary);
        }

        ListView playerScores = (ListView) view.findViewById(R.id.listPlayerScores);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, playerOverallScores);
        playerScores.setAdapter(adapter);

        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        public void onFragmentInteraction(Uri uri);
    }

}
