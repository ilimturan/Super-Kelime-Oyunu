package com.zargidigames.superkelimeoyunu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zargidigames.superkelimeoyunu.api.ApiConfig;
import com.zargidigames.superkelimeoyunu.api.ApiService;
import com.zargidigames.superkelimeoyunu.model.OptionLetter;
import com.zargidigames.superkelimeoyunu.model.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GamePlayActivity extends ActionBarActivity {

    private SharedPreferences userPreferences;
    private int levelCount;
    private int userLevel;

    private int questionActiveNumber = 0;
    private int questionCount = 0;
    private int userLevelScore = 0;
    private int userRemaningScore = 0;
    private int userJokerCount = 40;

    private int optionViewCount = 15;
    private int optionViewOpenCount = 0;

    private ProgressDialog progressDialog;
    private HashMap<Integer, Question> questions = new HashMap<>();
    private Question questionActive;

    private Button btnQuestionIndex;
    private Button btnQuestionScore;
    private Button btnSumScore;
    private Button btnJokerCount;
    private Button btnGetLetter;
    private Button btnAnswered;
    private Button btnNextQuestion;


    private TextView textQuestion;
    private TextView textLetter1;
    private TextView textLetter2;
    private TextView textLetter3;
    private TextView textLetter4;
    private TextView textLetter5;
    private TextView textLetter6;
    private TextView textLetter7;
    private TextView textLetter8;
    private TextView textLetter9;
    private TextView textLetter10;
    private TextView textLetter11;
    private TextView textLetter12;
    private TextView textLetter13;
    private TextView textLetter14;
    private TextView textLetter15;

    private FrameLayout frameAnswered;
    private Button btnUserAnswerCheck;
    private EditText textUserAnswer;
    private Animation frameSlideTop1;
    private Animation frameSlideTop2;

    private HashMap<Integer, TextView> optionletterViews = new HashMap<>();
    private HashMap<Integer, OptionLetter> optionLetters = new HashMap<>();

    private int screenWidth;
    private int screenHeight;

    private Chronometer chronometer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        handler = new Handler();

        userPreferences = getPreferences(MODE_PRIVATE);
        levelCount = userPreferences.getInt("levelCount", 15);
        userLevel = userPreferences.getInt("userLevel", 1);
        userJokerCount = userPreferences.getInt("userJokerCount", 40);

        showToastMessage("Level: "+userLevel);
        getLevel(userLevel);

    }

    private void getLevel(int levelId) {

        progressDialog = ProgressDialog.show(this, "Yükleniyor", "", true);
        getViewElements();
        getScreenSizes();
        setOptionsSizes();
        getDataFromApi(levelId);

    }

    private void getScreenSizes() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void setOptionsSizes() {
        for (int i = 1; i <= optionViewCount; i++) {

            TextView optionsView = optionletterViews.get(i);
            ViewGroup.LayoutParams layoutParams = optionsView.getLayoutParams();
            int optionsCalculated = (int) (screenWidth - 120) / 15;
            layoutParams.height = optionsCalculated;
            layoutParams.width = optionsCalculated;

            optionsView.setLayoutParams(layoutParams);
        }
    }

    private void setJokerCount() {
        btnJokerCount.setText("" + userJokerCount);
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    private void loadNextQuestion() {
        questionActiveNumber++;

        if (questions.get(questionActiveNumber) != null) {

            btnGetLetter.setClickable(true);
            optionViewOpenCount = 0;
            questionActive = questions.get(questionActiveNumber);

            btnQuestionIndex.setText("" + questionActiveNumber);
            btnQuestionScore.setText("" + questionActive.word_score);
            btnSumScore.setText("" + userLevelScore);
            btnJokerCount.setText("" + userJokerCount);

            textQuestion.setText(questionActive.description);
            userRemaningScore = questionActive.word_score;

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            setOptionLetterMaps();
            setOptionLetterViews();

        } else if (questionActiveNumber > questionCount) {
            loadLevelFinish();
        } else {
            showAlertDialog("Hata", "Sorularda hata var!");
        }

    }


    private void setOptionLetterMaps() {
        for (int k = 1; k <= questionActive.word.length(); k++) {
            OptionLetter optionLetter = new OptionLetter();
            optionLetter.letter = "" + questionActive.word.charAt(k - 1);
            optionLetter.isOpen = false;
            optionLetters.put(k, optionLetter);
        }

    }

    private void setOptionLetterViews() {
        for (int i = 1; i <= optionViewCount; i++) {
            if (i <= questionActive.word.length()) {
                optionletterViews.get(i).setVisibility(View.VISIBLE);
            } else {
                optionletterViews.get(i).setVisibility(View.GONE);
            }
            optionletterViews.get(i).setText("");

        }
    }

    private void loadLevelFinish() {

        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putInt("userLevel", userLevel);
        editor.putInt("userLevelScore_" + userLevel, userLevelScore);
        editor.commit();

        Intent intent = new Intent(this, GameLevelFinishActivity.class);
        startActivity(intent);
    }

    public void testNextQuestion(View v) {
        if (v.getId() == btnNextQuestion.getId()) {
            loadNextQuestion();
        }
    }

    public void getClicked(View v) {
        if (v.getId() == btnGetLetter.getId()) {
            getLetter();
        } else if (v.getId() == btnAnswered.getId()) {
            showAnswerFrame();
        } else if (v.getId() == btnUserAnswerCheck.getId()) {
            userAnswerButtonClicked();
        }
    }

    private void userAnswerButtonClicked() {

        String userAnswer = textUserAnswer.getText().toString();
        if (userAnswer.length() > 0) {
            hideKeybord();
            checkUserAnswer(userAnswer);

            textUserAnswer.setText("");
            frameAnswered.setVisibility(View.GONE);
            btnGetLetter.setClickable(true);
            btnAnswered.setClickable(true);
        }
    }


    private void getLetter() {
        if (userJokerCount > 0) {

            getRandomLetter();
        } else {
            showAlertDialog("Opps", "Harfleriniz bitti!");
        }

    }


    private void getRandomLetter() {



        int min = 1;
        int max = questionActive.word.length();
        Random random = new Random();
        int randIndex = random.nextInt((max - min) + 1) + min;

        if (optionViewOpenCount >= questionActive.letter_count) {
            sleepAndNextQuestion();

        } else if (optionLetters.get(randIndex).isOpen) {
            getRandomLetter();
        } else if(optionLetters.get(randIndex) != null) {
            userJokerCount--;
            optionViewOpenCount++;
            setJokerCount();
            setQuestionScore();
            optionLetters.get(randIndex).isOpen = true;
            optionletterViews.get(randIndex).setText(optionLetters.get(randIndex).letter.toUpperCase());

            if (optionViewOpenCount >= questionActive.letter_count) {
                sleepAndNextQuestion();
            }

        }else{
            showAlertDialog("Opps!", "Bir şeyler yanlış gidiyor. Soruyu bulamadık :(");
        }


    }

    private void sleepAndNextQuestion() {
        btnGetLetter.setClickable(false);
        showToastMessage("Sonraki soru");
        handler.postDelayed(new Runnable() {
            public void run() {
                loadNextQuestion();
            }
        }, 1000);
    }

    private void getViewElements() {
        btnQuestionIndex = (Button) findViewById(R.id.btn_question_index);
        btnQuestionScore = (Button) findViewById(R.id.btn_question_score);
        btnSumScore = (Button) findViewById(R.id.btn_sum_score);
        btnJokerCount = (Button) findViewById(R.id.btn_joker_count);

        btnGetLetter = (Button) findViewById(R.id.btn_get_letter);
        btnAnswered = (Button) findViewById(R.id.btn_answered);
        btnNextQuestion = (Button) findViewById(R.id.btn_next_question);

        textQuestion = (TextView) findViewById(R.id.text_question);
        textLetter1 = (TextView) findViewById(R.id.text_letter_1);
        textLetter2 = (TextView) findViewById(R.id.text_letter_2);
        textLetter3 = (TextView) findViewById(R.id.text_letter_3);
        textLetter4 = (TextView) findViewById(R.id.text_letter_4);
        textLetter5 = (TextView) findViewById(R.id.text_letter_5);
        textLetter6 = (TextView) findViewById(R.id.text_letter_6);
        textLetter7 = (TextView) findViewById(R.id.text_letter_7);
        textLetter8 = (TextView) findViewById(R.id.text_letter_8);
        textLetter9 = (TextView) findViewById(R.id.text_letter_9);
        textLetter10 = (TextView) findViewById(R.id.text_letter_10);
        textLetter11 = (TextView) findViewById(R.id.text_letter_11);
        textLetter12 = (TextView) findViewById(R.id.text_letter_12);
        textLetter13 = (TextView) findViewById(R.id.text_letter_13);
        textLetter14 = (TextView) findViewById(R.id.text_letter_14);
        textLetter15 = (TextView) findViewById(R.id.text_letter_15);

        optionletterViews.put(1, textLetter1);
        optionletterViews.put(2, textLetter2);
        optionletterViews.put(3, textLetter3);
        optionletterViews.put(4, textLetter4);
        optionletterViews.put(5, textLetter5);
        optionletterViews.put(6, textLetter6);
        optionletterViews.put(7, textLetter7);
        optionletterViews.put(8, textLetter8);
        optionletterViews.put(9, textLetter9);
        optionletterViews.put(10, textLetter10);
        optionletterViews.put(11, textLetter11);
        optionletterViews.put(12, textLetter12);
        optionletterViews.put(13, textLetter13);
        optionletterViews.put(14, textLetter14);
        optionletterViews.put(15, textLetter15);

        frameAnswered = (FrameLayout) findViewById(R.id.frame_answered);
        btnUserAnswerCheck = (Button) findViewById(R.id.btn_user_answer_check);
        textUserAnswer = (EditText) findViewById(R.id.text_user_answer);
        frameSlideTop1 = AnimationUtils.loadAnimation(this, R.anim.slide_top_1);
        frameSlideTop2 = AnimationUtils.loadAnimation(this, R.anim.slide_top_2);

        textUserAnswer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        chronometer = (Chronometer) findViewById(R.id.icon_timer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedSecond = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                int remaningSecond = (int) (30 - elapsedSecond);
                if (remaningSecond > 0) {
                    chronometer.setText("" + remaningSecond);
                } else {
                    showToastMessage("Süre bitti");
                    loadNextQuestion();
                }


            }
        });

        /**
         * Fix chronometer sizes
         */
        ViewGroup.LayoutParams btnJokerCountLayoutParams = btnJokerCount.getLayoutParams();
        ViewGroup.LayoutParams chronometerLayoutParams = chronometer.getLayoutParams();

        chronometerLayoutParams.height = btnJokerCountLayoutParams.height;
        chronometerLayoutParams.width = btnJokerCountLayoutParams.width;
        chronometer.setLayoutParams(chronometerLayoutParams);

    }

    private void getDataFromApi(final int levelId) {

        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(ApiConfig.API_BASE_URL).build();
        ApiService apiService = restAdapter.create(ApiService.class);
        apiService.getQuestions(levelId, new Callback<List<Question>>() {
            @Override
            public void success(List<Question> questions_, Response response) {
                if (response.getStatus() == 200 && questions_.size() > 0) {

                    int qIndex = 1;
                    for (Question q : questions_) {
                        questions.put(qIndex, q);
                        qIndex++;
                    }

                    questionCount = questions.size();
                    loadNextQuestion();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    showNotLoadedDialog("Hata!", "Sorular yüklenemedi.");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                showNotLoadedDialog("Hata!", "Sorular yüklenemedi.");
            }
        });
    }


    public void showAnswerFrame() {
        showKeybord();
        frameAnswered.clearAnimation();
        frameAnswered.setVisibility(View.VISIBLE);
        frameAnswered.setAnimation(frameSlideTop1);
        btnGetLetter.setClickable(false);
        btnAnswered.setClickable(false);

        textUserAnswer.setFocusableInTouchMode(true);
        textUserAnswer.requestFocus();
    }


    private void checkUserAnswer(String userAnswer_) {

        String userAnswer = userAnswer_.toLowerCase().trim();
        String trueAnswer = questionActive.word.toLowerCase().trim();

        if ((userAnswer.length() == trueAnswer.length()) && questionActive.word.contains(userAnswer)) {

            userLevelScore += userRemaningScore;
            showToastMessage("Cevap doğru");

            loadNextQuestion();
        } else {
            showToastMessage("Cevap yanlış");
        }
    }


    private void setQuestionScore() {
        if (userRemaningScore >= 100) {
            userRemaningScore = userRemaningScore - 100;
        }

        btnQuestionScore.setText("" + userRemaningScore);
    }


    public void hideKeybord() {

        if (getCurrentFocus() != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showKeybord() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void showNotLoadedDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                startMainMenuActivity();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.show();
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


    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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