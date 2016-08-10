package com.example.adam.activitytracker;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adam on 22/03/2015.
 */

public class MapsActivity extends FragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //declaring image buttons
    private static ImageButton btnStart;
    private static ImageButton btnStop;

    //declaring google maps and location services components
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // declaring variables for the activity
    private boolean firstStart = true;
    private boolean tracking = false;
    private ArrayList<LatLng> points = new ArrayList<LatLng>();
    private PolylineOptions polylineOptions;
    private float[] results = new float[1];
    private float distance;
    private LatLng oldLoc;

    // declaring variables for the timer
    long lStartTime;
    long lEndTime;
    long difference = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // Retrieve extended data from the intent that started this activity.
        final String getMode = (getIntent().getStringExtra("mode"));
        String getData = (getIntent().getStringExtra("typeOfTRacking"));
        int updateInterval = Integer.parseInt(getData);

        // To set up the request for the fused location provider, we need to create a LocationRequest and GoogleApiClient
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(updateInterval); // in milliseconds
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // get pointers to the views defined in activity_maps.xml
        btnStart = (ImageButton) findViewById(R.id.imgBtnStart);
        btnStop = (ImageButton) findViewById(R.id.imgBtnStop);

        // create a button click listener for the start button
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start tracking and stopwatch
                tracking = true;
                lStartTime = new Date().getTime();

                Toast.makeText(getApplicationContext(), "Tracking on.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // create a button click listener for the stop button
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // stop tracking and stopwatch
                tracking = false;
                lEndTime = new Date().getTime();
                difference = lEndTime - lStartTime;

                // explicitly start activity the summary activity, additional information using putExtra
                Intent goShowWorkout = new Intent(MapsActivity.this, ShowWorkout.class);
                goShowWorkout.putExtra("points", points);
                goShowWorkout.putExtra("distance", distance);
                goShowWorkout.putExtra("time", difference);
                goShowWorkout.putExtra("mode", getMode);
                startActivity(goShowWorkout);
                finish();
            }
        });
    }

    // draw line on map
    private void drawPolyline(LatLng point){
            // add location coordinates to the list
            polylineOptions = new PolylineOptions().color(Color.RED).width(5);
            points.add(point);
            for(int i=0;i<points.size();i++){
                polylineOptions.add(points.get(i));
            }
            // draw line on the map
            mMap.addPolyline(polylineOptions);
    }

    // calculate the distance
    private void calcDistance(LatLng point){
        float diff;
        // the actual location is stored in newLoc
        LatLng newLoc = point;
        // calculates the distance
        Location.distanceBetween(oldLoc.latitude, oldLoc.longitude, newLoc.latitude, newLoc.longitude, results);
        // the actual location will be the old location next time
        oldLoc = newLoc;
        diff = results[0];
        // total total distance is calculated
        distance = distance+diff;
        Toast.makeText(getApplicationContext(), "Total distance is: "+Float.toString((distance)),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
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

    protected void startLocationUpdates() {
        PendingResult<Status> statusPendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

        // current location is stored in a location object
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // this method is used to zoom in to the current location the first tme the map loads
        if (firstStart == true)
        {
            // Moves the camera to users current longitude and latitude
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Animates camera and zooms to preferred state on the user's current location.
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,(float) 17));
            // location must be stored as oldLoc otherwise the program crashes
            oldLoc = latLng;
            // call calcDistance method
            calcDistance(latLng);
            firstStart = false;
        }

        // if tracking is on
        if (tracking == true) {
            // follow the actual location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            calcDistance(latLng);
            // draw polyline
            drawPolyline(latLng);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
                /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
}
