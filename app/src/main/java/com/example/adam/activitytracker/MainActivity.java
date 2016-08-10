package com.example.adam.activitytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Adam on 22/03/2015.
 */

public class MainActivity extends ActionBarActivity {

    // Declaring variables
    private static ImageButton btnWalking;
    private static ImageButton btnRunning;
    private static ImageButton btnCycling;
    private static ImageButton btnWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get pointers to the views defined in activity_main.xml
        btnWalking = (ImageButton) findViewById(R.id.imgBtnWalking);
        btnRunning = (ImageButton) findViewById(R.id.imgBtnRunning);
        btnCycling = (ImageButton) findViewById(R.id.imgBtnCycling);
        btnWorkouts = (ImageButton) findViewById(R.id.imgBtnWorkouts);

        // create button click listeners for the buttons
        btnWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // explicitly start activity, additional information using putExtra
                Intent goWalking = new Intent(MainActivity.this, MapsActivity.class);
                goWalking.putExtra("typeOfTRacking", "3000");
                goWalking.putExtra("mode", "Walking");
                startActivity(goWalking);
            }
        });

        // create button click listeners for the buttons
        btnRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // explicitly start activity, additional information using putExtra
                Intent goRunning = new Intent(MainActivity.this, MapsActivity.class);
                goRunning.putExtra("typeOfTRacking", "2000");
                goRunning.putExtra("mode", "Running");
                startActivity(goRunning);
            }
        });

        // create button click listeners for the buttons
        btnCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // explicitly start activity, additional information using putExtra
                Intent goCycling = new Intent(MainActivity.this, MapsActivity.class);
                goCycling.putExtra("typeOfTRacking", "1000");
                goCycling.putExtra("mode", "Cycling");
                startActivity(goCycling);
            }
        });

        // create button click listeners for the buttons
        btnWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // explicitly start activity, additional information using putExtra
                Intent goWorkouts = new Intent(MainActivity.this, WorkoutListActivity.class);
                startActivity(goWorkouts);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // declaring variables for exit method
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    // exit method
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if back button is pressed
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    // if back button is pressed again within 2 secs, exit
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        finish();
                    }
                    // if not, displays text
                    else {
                        Toast.makeText(getApplicationContext(), "Press again to exit.",
                                Toast.LENGTH_SHORT).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }
}
