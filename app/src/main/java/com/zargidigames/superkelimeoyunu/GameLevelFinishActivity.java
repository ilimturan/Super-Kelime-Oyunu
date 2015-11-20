package com.zargidigames.superkelimeoyunu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zargidigames.superkelimeoyunu.config.GameConfig;

import java.util.HashMap;

public class GameLevelFinishActivity extends ActionBarActivity {

    private SharedPreferences userPreferences;
    private int levelCount;
    private int userLevel;
    private int userJokerCount;
    private int userLevelScore;
    private HashMap<Integer, Integer> userLevelScores = new HashMap<>();

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

        //userPreferences = getPreferences(MODE_PRIVATE);
        userPreferences = getApplicationContext().getSharedPreferences(GameConfig.GAME_PREF, 0);

        levelCount = userPreferences.getInt("levelCount", 15);
        userLevel = userPreferences.getInt("userLevel", 1);
        userJokerCount = userPreferences.getInt("userJokerCount", 40);
        userLevelScore = userPreferences.getInt("userLevelScore_" + userLevel, 0);
        getViewElements();
        checkGameOver();

    }

    private void getUserLevelScores() {

        for (int i = 1; i <= levelCount; i++) {
            int levelScore = userPreferences.getInt("userLevelScore_" + i, 0);
            userLevelScores.put(i, levelScore);
            Log.d("ilimdebug", "" + i + "->" + levelScore);
        }

    }

    private void getViewElements() {

        btnFinishSave = (Button) findViewById(R.id.btn_finis_save);
        btnFinishNewGame = (Button) findViewById(R.id.btn_finish_new_game);
        btnFinishShare = (Button) findViewById(R.id.btn_finish_share);
        textFinishResult = (TextView) findViewById(R.id.text_finis_result);
        textFinishResult.setText("" + userLevelScore);
    }

    public void getClicked(View v) {

        if (v.getId() == btnFinishSave.getId()) {
            saveUserLevelResult();
        } else if (v.getId() == btnFinishNewGame.getId()) {
            startNewGame();
        } else if (v.getId() == btnFinishShare.getId()) {
            shareUserResult();
        }
    }

    private void shareUserResult() {
        //TODO ilim
    }

    private void checkGameOver() {

        if (userLevel >= levelCount) {
            showAlertDialog(getString(R.string.title_game_over_congratulations), getString(R.string.message_game_over_congratulations));
            SharedPreferences.Editor editor = userPreferences.edit();
            editor.putInt("levelCount", levelCount);
            editor.putInt("userJokerCount", 40);
            editor.putInt("userLevel", 1);
            editor.commit();
            getUserLevelScores();
        }
    }

    private void startNewGame() {
        userLevel++;
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putInt("levelCount", levelCount);
        editor.putInt("userJokerCount", userJokerCount);
        editor.putInt("userLevel", userLevel);
        editor.commit();
        startGameActivity();

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

    private void saveUserLevelResult() {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putInt("userLevelScore_" + userLevel, userLevelScore);
        editor.commit();
        showAlertDialog(getString(R.string.title_save_level_result), getString(R.string.message_save_level_result));
        btnFinishSave.setClickable(false);
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
