package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameSetupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameSetupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> playerNames;
    private ArrayAdapter<String> adapter;
    private ListView playerList;
    private EditText txtPlayerName;

    public GameSetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameSetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameSetupFragment newInstance(String param1, String param2) {
        GameSetupFragment fragment = new GameSetupFragment();
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
        View view = inflater.inflate(R.layout.fragment_game_setup, container, false);

        FloatingActionButton floatingActionButtonAddPlayer = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonAddPlayer);

        floatingActionButtonAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("playerNames", playerNames);
                AddPlayerFragment addPlayerFragment = new AddPlayerFragment();
                addPlayerFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerGameSetup, addPlayerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        FloatingActionButton floatingActionButtonStartGame = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonStart);
        floatingActionButtonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Player List", playerNames);
                Intent intent = new Intent(getActivity(), ScoringTableActivity.class);
                intent.putExtra("Player Bundle", bundle);
                startActivity(intent);
            }
        });

        if(getArguments() != null){
            playerNames = getArguments().getStringArrayList("playerNames");
        } else {
            playerNames = new ArrayList<>();
        }

        playerList = (ListView) view.findViewById(R.id.listPlayer);

        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = playerList.getItemAtPosition(position).toString();
                txtPlayerName.setText(name);
            }
        });

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, playerNames);
        playerList.setAdapter(adapter);

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

    /*public void onClick(View v){
        switch(v.getId()){
            case R.id.btnAddPlayer:
                if(!txtPlayerName.getText().toString().equals("") && txtPlayerName.getText().toString() != null){
                    playerNames.add(txtPlayerName.getText().toString());
                    playerList.invalidateViews();
                } else {
                    Toast.makeText(getContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRemovePlayer:
                if(!txtPlayerName.getText().toString().equals("") && txtPlayerName.getText().toString() != null){
                    playerNames.remove(txtPlayerName.getText().toString());
                    playerList.invalidateViews();
                } else {
                    Toast.makeText(getContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnClear:
                playerNames.clear();
                playerList.invalidateViews();
                break;
            case R.id.btnStartGame:

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Player List", playerNames);
                Intent intent = new Intent(getActivity(), ScoringTableActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }*/

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
