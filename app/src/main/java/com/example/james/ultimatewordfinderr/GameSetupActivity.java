package com.example.james.ultimatewordfinderr;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class GameSetupActivity extends AppCompatActivity implements GameSetupFragment.OnFragmentInteractionListener, AddPlayerFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.game_setup_activity_toolbar);
        setSupportActionBar(toolbar);

        GameSetupFragment gameSetupFragment = new GameSetupFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containerGameSetup, gameSetupFragment);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHelpFeedback:
                HelpFeedbackFragment helpFeedbackFragment = new HelpFeedbackFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerGameSetup, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.aboutApp:
                Intent aboutAppIntent = new Intent(this, AboutAppActivity.class);
                startActivity(aboutAppIntent);

                return true;
            case R.id.acknowledgements:
                Intent acknowledgementsIntent = new Intent(this, CreditsActivity.class);
                startActivity(acknowledgementsIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteractionAddPlayer(ArrayList<Player> players) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("players", players);
        GameSetupFragment gameSetupFragment = new GameSetupFragment();
        gameSetupFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerGameSetup, gameSetupFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String option) {

    }
}
