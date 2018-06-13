package com.example.james.ultimatewordfinderr;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BugReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BugReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BugReportFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String priority;

    private ProgressDialog progressDialog;

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
                switch (checkedId) {
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
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                GitHubIssue gitHubIssue = new GitHubIssue(title, description, priority);
                new CreateIssueOnGitHubTask(gitHubIssue).execute();
            }
        });


        return view;
    }

    private void setPriority(String priority) {
        this.priority = priority;
    }

    private class CreateIssueOnGitHubTask extends AsyncTask<Void, Void, Void> {

        private GitHubIssue gitHubIssue;

        public CreateIssueOnGitHubTask(GitHubIssue gitHubIssue) {
            this.gitHubIssue = gitHubIssue;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject json = new JsonObject();
            json.addProperty("title", "[" + gitHubIssue.getPriority() + "] " + gitHubIssue.getTitle());
            json.addProperty("description", gitHubIssue.getDescription());

            // TODO: Integrate custom backend
            Ion.with(getActivity())
                    .load("https://example.com/backend")
                    .setHeader("Content-Type", "application/json")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }

                            if (result != null) {
                                Toast.makeText(getActivity(), "Bug report uploaded successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                Log.e("GitHubIssue Error", e.getMessage());
                            }
                        }
                    });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
