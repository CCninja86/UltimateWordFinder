package com.example.james.ultimatewordfindernew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameSetupActivity extends AppCompatActivity {

    private ArrayList<String> playerNames;
    private ArrayAdapter<String> adapter;
    private ListView playerList;
    private EditText txtPlayerName;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        playerNames = new ArrayList<>();

        if(savedInstanceState != null){
            playerNames = savedInstanceState.getStringArrayList("Players");
        }

        playerList = (ListView) findViewById(R.id.listPlayer);
        txtPlayerName = (EditText) findViewById(R.id.txtPlayerName);
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = playerList.getItemAtPosition(position).toString();
                txtPlayerName.setText(name);
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerNames);
        playerList.setAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
       super.onSaveInstanceState(savedState);

        savedState.putStringArrayList("Players", playerNames);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnAddPlayer:
                if(!txtPlayerName.getText().toString().equals("") && txtPlayerName.getText().toString() != null){
                    playerNames.add(txtPlayerName.getText().toString());
                    playerList.invalidateViews();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRemovePlayer:
                if(!txtPlayerName.getText().toString().equals("") && txtPlayerName.getText().toString() != null){
                    playerNames.remove(txtPlayerName.getText().toString());
                    playerList.invalidateViews();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnClear:
                playerNames.clear();
                playerList.invalidateViews();
                break;
            case R.id.btnStartGame:

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Player List", playerNames);
                Intent intent = new Intent(this, ScoringTableActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
