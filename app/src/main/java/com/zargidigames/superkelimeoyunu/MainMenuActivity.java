package com.zargidigames.superkelimeoyunu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.zargidigames.superkelimeoyunu.api.ApiConfig;
import com.zargidigames.superkelimeoyunu.api.ApiService;
import com.zargidigames.superkelimeoyunu.model.Level;
import com.zargidigames.superkelimeoyunu.model.Question;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainMenuActivity extends ActionBarActivity {

    private SharedPreferences userPreferences;
    private int levelCount;
    private int userLevel;
    private int userJokerCount;

    private final String language = "turkish";
    private List<Level> levels = new ArrayList<>();
    private ImageView mainGameLogo;
    private ImageButton playButton;
    private Animation shakeAnim1;
    private Animation shakeAnim2;
    private Animation slideLeftAnim1;
    private Animation slideLeftAnim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userPreferences = getPreferences(MODE_PRIVATE);
        levelCount = userPreferences.getInt("levelCount", 15);
        userLevel = userPreferences.getInt("userLevel", 1);
        userJokerCount = userPreferences.getInt("userJokerCount", 40);

        mainGameLogo = (ImageView) findViewById(R.id.main_game_logo);
        playButton = (ImageButton) findViewById(R.id.button_play);
        shakeAnim1 = AnimationUtils.loadAnimation(this, R.anim.shake1);
        shakeAnim2 = AnimationUtils.loadAnimation(this, R.anim.shake2);
        slideLeftAnim1 = AnimationUtils.loadAnimation(this, R.anim.slide_left_1);
        slideLeftAnim2 = AnimationUtils.loadAnimation(this, R.anim.slide_left_2);

        showAnimation();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == playButton.getId()) {
                    startGameActivity();
                }
            }
        });
        getLevelsFromApi(language);
    }

    private void getLevelsFromApi(final String language) {

        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(ApiConfig.API_BASE_URL).build();
        ApiService apiService = restAdapter.create(ApiService.class);

        apiService.getLevels(language, new Callback<List<Level>>() {
            @Override
            public void success(List<Level> levels_, Response response) {
                if (response.getStatus() == 200 && levels_.size() > 0) {

                    levels = levels_;
                    levelCount = levels.size();

                    Log.d("ilimdebug", "levels->"+levels.toString());
                    Log.d("ilimdebug", "levelCount->" + levelCount);
                } else {

                    showNotLoadedDialog("Hata!", "Oyun yüklenemedi.");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showNotLoadedDialog("Hata!", "Oyun yüklenemedi, internet bağlantınızı konrol ediniz.");
            }
        });

    }

    private void showNotLoadedDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                getLevelsFromApi(language);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showAnimation() {

        slideLeftAnim1.setStartOffset(300);
        slideLeftAnim2.setStartOffset(500);

        mainGameLogo.clearAnimation();
        mainGameLogo.setAnimation(slideLeftAnim1);

        playButton.clearAnimation();
        playButton.setAnimation(slideLeftAnim2);

        slideLeftAnim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                playButton.setAnimation(shakeAnim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startGameActivity() {

        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putInt("levelCount", levelCount);
        editor.commit();

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
