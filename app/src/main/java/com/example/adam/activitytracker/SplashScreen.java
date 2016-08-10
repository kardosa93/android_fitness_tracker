package com.example.adam.activitytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Adam on 15/03/2015.
 */
public class SplashScreen extends Activity {

    // set delay
    private static int SPLASH_SCREEN_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);

        // show splash screen and open main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
