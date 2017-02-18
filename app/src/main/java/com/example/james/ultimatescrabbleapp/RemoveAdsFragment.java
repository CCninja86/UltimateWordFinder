package com.example.james.ultimatescrabbleapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoveAdsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemoveAdsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemoveAdsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private IInAppBillingService mService;
    private ServiceConnection mServiceConnection;

    private LinearLayout linearLayout;

    private View.OnClickListener onClickListener;

    private ArrayList<String> skuList;
    private Bundle querySkus;
    private Bundle skuDetails;

    public RemoveAdsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemoveAdsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemoveAdsFragment newInstance(String param1, String param2) {
        RemoveAdsFragment fragment = new RemoveAdsFragment();
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
        final View view = inflater.inflate(R.layout.fragment_remove_ads, container, false);


        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                getIAPs(view);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


        return view;
    }

    private void getIAPs(View view){



        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnOneDollar:
                        skuList = new ArrayList<>();
                        skuList.add("remove_ads_1");
                        querySkus = new Bundle();
                        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    skuDetails = mService.getSkuDetails(3,
                                            getActivity().getPackageName(), "inapp", querySkus);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        int responseCode = skuDetails.getInt("RESPONSE_CODE");

                        if(responseCode == 0){
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                            String skuId = null;
                            String price = null;

                            for (String response : responseList) {
                                JSONObject object = null;

                                try {
                                    object = new JSONObject(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                                try {
                                    skuId = object.getString("productId");
                                    price = object.getString("price");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), skuId, "inapp", "price1");
                                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
        };

        Button btnOneDollar = (Button) view.findViewById(R.id.btnOneDollar);
        Button btnTwoDollar = (Button) view.findViewById(R.id.btnTwoDollars);
        Button btnFiveDollar = (Button) view.findViewById(R.id.btnFiveDollars);
        Button btnTenDollar = (Button) view.findViewById(R.id.btnTenDollars);
        Button btnFifteenDollar = (Button) view.findViewById(R.id.btnFifteenDollars);
        Button btnTwentyDollar = (Button) view.findViewById(R.id.btnTwentyDollars);



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

        if(mService != null){
            getActivity().unbindService(mServiceConnection);
        }
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
