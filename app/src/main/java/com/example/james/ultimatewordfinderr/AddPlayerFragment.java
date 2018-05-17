package com.example.james.ultimatewordfinderr;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPlayerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<Player> players;
    private ArrayList<Integer> coloursList;

    public AddPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPlayerFragment.
     */

    public static AddPlayerFragment newInstance(String param1, String param2) {
        AddPlayerFragment fragment = new AddPlayerFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_player, container, false);

        Bundle bundle = getArguments();
        players = (ArrayList<Player>) bundle.getSerializable("players");

        int[] colourCodes = getResources().getIntArray(R.array.materialColours);
        coloursList = new ArrayList<>();

        for (int code : colourCodes) {
            coloursList.add(code);
        }

        final Button btnChooseColour = (Button) view.findViewById(R.id.btnChooseColour);
        final EditText editTextPlayerName = (EditText) view.findViewById(R.id.editTextPlayerName);

        btnChooseColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPickerDialog dialog = ColorPickerDialog.newInstance(ColorPickerDialog.SELECTION_SINGLE, coloursList, 4, ColorPickerDialog.SIZE_SMALL);
                dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
                    @Override
                    public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                        int selectedColour = dialog.getSelectedColors().get(0);
                        String hexColor = String.format("#%06X", (0xFFFFFF & selectedColour));
                        btnChooseColour.setBackgroundColor(selectedColour);
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                dialog.show(getFragmentManager(), "colour_picker");
            }
        });

        final Button btnAddPlayer = (Button) view.findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColour = (ColorDrawable) btnChooseColour.getBackground();

                Player player = new Player(editTextPlayerName.getText().toString().trim(), buttonColour.getColor());
                players.add(player);
                mListener.onFragmentInteractionAddPlayer(players);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteractionAddPlayer(ArrayList<Player> players);
    }
}
