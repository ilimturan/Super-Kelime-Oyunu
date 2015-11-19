package com.zargidigames.superkelimeoyunu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameLevelFinishActivity extends ActionBarActivity {

    private SharedPreferences userPreferences;
    private int levelCount;
    private int userLevel;
    private int userJokerCount;
    private int userLevelScore;
    //private HashMap<Integer, Integer> userLevelScores = new HashMap<>();

    private Button btnFinishSave;
    private Button btnFinishNewGame;
    private Button btnFinishShare;
    private TextView textFinishResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level_finish);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userPreferences = getPreferences(MODE_PRIVATE);
        levelCount = userPreferences.getInt("levelCount", 15);
        userLevel = userPreferences.getInt("userLevel", 1);
        userJokerCount = userPreferences.getInt("userJokerCount", 40);

        userLevelScore = userPreferences.getInt("userLevelScore_" + userLevel, 0);

        //showAlertDialog("Results: ", "levelCount->"+levelCount+"/n userLevel"+userLevel+"/n userLevelScore "+userLevelScore);

        getViewElements();
    }

    private void getViewElements() {
        btnFinishSave = (Button) findViewById(R.id.btn_finis_save);
        btnFinishNewGame = (Button) findViewById(R.id.btn_finish_new_game);
        btnFinishShare = (Button) findViewById(R.id.btn_finish_share);
        textFinishResult = (TextView) findViewById(R.id.text_finis_result);

        textFinishResult.setText(""+userLevelScore);
    }

    public void getClicked(View v) {
        if (v.getId() == btnFinishSave.getId()) {
            saveUserResult();
        } else if (v.getId() == btnFinishNewGame.getId()) {
            startNewGame();
        } else if (v.getId() == btnFinishShare.getId()) {
            shareUserResult();
        }
    }

    private void shareUserResult() {

    }

    private void startNewGame() {
        userLevel++;

        if (userLevel <= levelCount) {
            SharedPreferences.Editor editor = userPreferences.edit();
            editor.putInt("levelCount", levelCount);
            editor.putInt("userLevel", userLevel);
            editor.commit();

            startGameActivity();
        } else {
            showAlertDialog("Tebrikler!", "Oyunu tamamen bitirdiniz.");
        }


    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //checkActiveNetwork();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private void saveUserResult() {
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GamePlayActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }
}
