package com.example.adam.activitytracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Adam on 22/03/2015.
 */

public class ShowWorkout extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private PolylineOptions polylineOptions;
    LatLngBounds.Builder builder;

    //declaring image buttons and text views
    private static TextView txtDuration;
    private static TextView txtDistance;
    private static TextView txtSpeed;
    private static ImageButton btnSave;

    //declaring variables for the activity
    private String convertedAvg;
    private String convertedDistance;
    private String convertedTime;
    private String convertedMode;

    // Initialise the database objects
    private DatabaseHelper databaseHelper;
    private RemoteDatabaseHelper remoteDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wokout_layout);
        setUpMapIfNeeded();

        // get pointers to the views defined in workout_layout.xml
        txtDuration = (TextView) findViewById(R.id.dataDuration);
        txtDistance = (TextView) findViewById(R.id.dataDistance);
        txtSpeed = (TextView) findViewById(R.id.dataSpeed);
        btnSave = (ImageButton) findViewById(R.id.imgBtnSave);

        // Initialise the database objects
        databaseHelper = new DatabaseHelper(this);
        remoteDatabaseHelper = new RemoteDatabaseHelper(this);

        // create a button click listener for the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cal addworkout method -> populate database
                addWorkout(convertedMode ,convertedDistance, convertedTime, convertedAvg);
                Toast.makeText(getApplicationContext(), "The workout has been saved",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // first the map has to be loaded, this event occurs after being loaded
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                // Move camera.
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
                // Remove listener to prevent position reset on camera move.
                mMap.setOnCameraChangeListener(null);
            }
        });

    }

    // draw line on map
    private void drawPolyline(ArrayList<LatLng> point){
        // add location coordinates to the list
        polylineOptions = new PolylineOptions().color(Color.RED).width(5);
        for(int i=0;i<point.size();i++){
            polylineOptions.add(point.get(i));
        }
        // draw line on the map
        mMap.addPolyline(polylineOptions);
    }

    // move camera to show line on the map
    public void positionMap(ArrayList<LatLng> point){

        // calculate the bounds of all the points
        builder = new LatLngBounds.Builder();
        for (LatLng marker : point) {
            builder.include(marker);
        }
        LatLngBounds bounds = builder.build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    // setup the screen, display info
    private void dataSetup() {
        // Retrieve extended data from the intent that started this activity.
        Bundle extras = getIntent().getExtras();
        ArrayList<LatLng> showPoints = (ArrayList<LatLng>) extras.getSerializable("points");
        Float distance = (Float) extras.getSerializable("distance");
        String mode = (String) extras.getSerializable("mode");
        long time = (Long) extras.getSerializable("time");
        double avg = ((double)Math.round(distance * 100) / 100)/(time/1000);

        // convert the info into strings
        convertedAvg = String.valueOf((double)Math.round(avg * 100) / 100);
        convertedDistance = String.valueOf(((double)Math.round(distance * 100) / 100));
        convertedTime = String.valueOf(time/1000);
        convertedMode = mode;

        // set the textview to display the converted strings
        ((TextView)findViewById(R.id.dataDistance)).setText(convertedDistance + " m");
        ((TextView)findViewById(R.id.dataDuration)).setText(convertedTime + " secs");
        ((TextView)findViewById(R.id.dataSpeed)).setText(convertedAvg+ " m/s");

        // draw polyline on the map
        drawPolyline(showPoints);
        // move map to show line
        positionMap(showPoints);
    }

    // called when button is pressed
    private void addWorkout(String convertedMode, String convertedDistance, String convertedTime, String convertedAvg) {
        // get the actual date and time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        // save the data as string
        String name = convertedMode+" "+formattedDate;
        String dist = "Distance: "+convertedDistance+" m";
        String dur = "Duration: "+convertedTime+" secs";
        String spd = "Avg Speed: "+convertedAvg+" m/s";

        /* Construct a WorkoutDetails object and pass it to the helper for database insertion */
        databaseHelper.addWorkout(new WorkoutDetails(name, dist, dur, spd));
        remoteDatabaseHelper.addWorkout(new WorkoutDetails(name, dist, dur, spd));

    }

    private void setUpMap() {
        dataSetup();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
}
