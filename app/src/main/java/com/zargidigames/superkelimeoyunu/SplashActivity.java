package com.zargidigames.superkelimeoyunu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SplashActivity extends ActionBarActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                    }

                }
            };
            thread.start();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            return false;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return false;
        }

        @Override
        protected void onPause() {
            super.onPause();
            finish();
        }
    }

