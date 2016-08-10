package com.example.adam.activitytracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Adam on 22/03/2015.
 */
public class WorkoutListActivity extends Activity {

    // declare buttons, listview and database adapters
    private DatabaseHelper databaseHelper;
    private RemoteDatabaseHelper remoteDatabaseHelper;
    private ListView list;
    private static ImageButton btnBackup;
    private static ImageButton btnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // get pointers to the views defined in activit_list.xml
        btnRestore = (ImageButton) findViewById(R.id.imgBtnRestore);
        list = (ListView)findViewById(R.id.list_workouts);

        // set onitemclicklistener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3)
            {
                deleteWorkoutAndRefresh(view);
            }
        });

        // Initialise the database objects
        databaseHelper = new DatabaseHelper(this);
        remoteDatabaseHelper = new RemoteDatabaseHelper(this);

        // populate list method to display workouts
        populateList();
    }

    private void populateList(){
        /* Get workouts list from helper. */
        ArrayList<WorkoutDetails> workouts = databaseHelper.getWorkoutsList();

        /* Create a list adapter bound to the workout list. */
        WorkoutsAdapter adapter = new WorkoutsAdapter(this, workouts);

        /* Attach the adapter to our list view. */
        list.setAdapter(adapter);
    }

    private void deleteWorkoutAndRefresh(View view){
        /* Get text values from child views. */
        String name = ((TextView)view.findViewById(R.id.display_name)).getText().toString();
        String dis = ((TextView)view.findViewById(R.id.display_distance)).getText().toString();
        String dur = ((TextView)view.findViewById(R.id.display_duration)).getText().toString();
        String spd = ((TextView)view.findViewById(R.id.display_speed)).getText().toString();

        /* Query the database. */
        int result = databaseHelper.removeWorkout(new WorkoutDetails(name, dis, dur, spd));

        /* Display toast notifying user of the number of deleted rows.  */
        Toast.makeText(WorkoutListActivity.this, result + " workout was deleted from the database.", Toast.LENGTH_SHORT).show();

        /* Refresh the list of workouts. */
        populateList();
    }

    public void downloadBackup(View view) {
        /* Download workout from remote database. */
        ArrayList<WorkoutDetails> workouts = remoteDatabaseHelper.getWorkoutsList();
        int count = 0;
        for(int i = 0; i < workouts.size(); i++){
            WorkoutDetails d = workouts.get(i);
            if(!databaseHelper.contactExists(d)) {

                databaseHelper.addWorkout(d);
                /* Count added workouts. */
                count++;
            }
        }
        populateList();
        Toast.makeText(this, count + " workouts were restored!", Toast.LENGTH_SHORT).show();
    }
}
