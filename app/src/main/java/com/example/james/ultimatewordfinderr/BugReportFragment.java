package com.example.james.ultimatewordfinderr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BugReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BugReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BugReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String priority;

    public BugReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BugReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BugReportFragment newInstance(String param1, String param2) {
        BugReportFragment fragment = new BugReportFragment();
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
        View view = inflater.inflate(R.layout.fragment_bug_report, container, false);

        final EditText editTextTitle = (EditText) view.findViewById(R.id.editTextSubject);
        final EditText editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupPriority);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButtonLow:
                        setPriority("LOW");
                        break;
                    case R.id.radioButtonMedium:
                        setPriority("MEDIUM");
                        break;
                    case R.id.radioButtonHigh:
                        setPriority("HIGH");
                        break;

                }
            }
        });


        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Will figure out Trello API later

                /*TrelloCard trelloCard = new TrelloCard("[BUG]" + textViewTitle.getText().toString(), textViewDescription.getText().toString(), "bottom", null, "58904d9b0fff4d29dab1fe0e");
                Gson gson = new Gson();
                String json = gson.toJson(trelloCard);

                TrelloAPITask trelloAPITask = new TrelloAPITask("https://api.trello.com/1/cards", "POST", "application/json", json);
                trelloAPITask.execute();*/

                // In the meantime, just send an email

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ccninja86developer@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[BUG] [" + priority + "] " + editTextTitle.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });




        return view;
    }

    private class TrelloAPITask extends AsyncTask<Void, Void, Void> {

        String url;
        String requestMethod;
        String contentType;
        String body;
        ProgressDialog progressDialog;

        public TrelloAPITask(String url, String requestMethod, String contentType, String body){
            this.url = url;
            this.requestMethod = requestMethod;
            this.contentType = contentType;
            this.body = body;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Submitting...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpsURLConnection connection;

            try {
                URL connUrl = new URL(url);
                connection = (HttpsURLConnection) connUrl.openConnection();
                connection.setRequestMethod(requestMethod);
                connection.addRequestProperty("Content-Type", contentType);
                connection.setRequestProperty("Content-Length", body);
                connection.getOutputStream().write(body.getBytes("UTF8"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void setPriority(String priority){
        this.priority = priority;
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
