package com.example.james.ultimatescrabbleapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class InterstitialAdActivity extends AppCompatActivity {

    InterstitialAd interstitialAd;
    ProgressDialog progressDialog;
    String user = "me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);



        if(user != null && user.equals("me")){
            Intent intent = new Intent(getApplicationContext(), GameSetupActivity.class);
            startActivity(intent);
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Intent intent = new Intent(getApplicationContext(), GameSetupActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAdLoaded(){
                    if(progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    interstitialAd.show();
                }
            });

            requestNewInterstitial();
        }
    }

    private void requestNewInterstitial(){
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("72A09D092AFFDE0C64546FD216A276F4").build();
        interstitialAd.loadAd(adRequest);
    }
}
