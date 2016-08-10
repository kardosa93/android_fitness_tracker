package com.example.adam.activitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Adam on 22/03/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /* Initialise constants. */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SQLiteLabDB";
    private static final String WORKOUTS_TABLE_NAME = "workouts";
    private static final String[] COLUMN_NAMES = {"Name", "Distance", "Duration", "Speed"};
    /* Construct CREATE query string. */
    private static final String WORKOUTS_TABLE_CREATE =
            "CREATE TABLE " + WORKOUTS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " TEXT, " +
                    COLUMN_NAMES[1] + " TEXT, " +
                    COLUMN_NAMES[2] + " TEXT, " +
                    COLUMN_NAMES[3] + " TEXT);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the database if it doesn't exist and adds the "workouts" table.
        /* Execute SQL query. */
        db.execSQL(WORKOUTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // can be left empty for the purposes of our tutorial
    }

    public void addWorkout(WorkoutDetails c) {
        /* Pack workout details in ContentValues object for database insertion. */
        ContentValues row = new ContentValues();
        row.put(this.COLUMN_NAMES[0], c.name);
        row.put(this.COLUMN_NAMES[1], c.distance);
        row.put(this.COLUMN_NAMES[2], c.duration);
        row.put(this.COLUMN_NAMES[3], c.speed);
        // The first parameter is a column name, the second is a value.

        /* Get writable database and insert the new row to the "workouts" table. */
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(WORKOUTS_TABLE_NAME, null, row);
        db.close();
    }

    public ArrayList<WorkoutDetails> getWorkoutsList() {
        /* Get the readable database. */
        SQLiteDatabase db = this.getReadableDatabase();

        /* Get all contacts by querying the database. */
        Cursor result = db.query(WORKOUTS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

        /* Convert results to a list of workouts objects. */
        ArrayList<WorkoutDetails> workouts = new ArrayList<WorkoutDetails>();

        for (int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            /* Create a workouts object with using data from name, distance, duration, speed Add it to list. */
            workouts.add(new WorkoutDetails(result.getString(0), result.getString(1), result.getString(2), result.getString(3)));
        }

        return workouts;
    }

    public boolean contactExists(WorkoutDetails details){
        /* Check if workout exists in the local database. */
        ArrayList<WorkoutDetails> existingContacts = getWorkoutsList();
        for(int i = 0; i < existingContacts.size(); i++){
            /* Go through all existing workouts and compare the details to a given workout. */
            WorkoutDetails d = existingContacts.get(i);
            if(d.name.equals(details.name) && d.distance.equals(details.distance) && d.duration.equals(details.duration) && d.speed.equals(details.speed)){
                /* If everything matches up, return true. */
                return true;
            }
        }
        /* If nothing matches up, return false. */
        return false;
    }

    public int removeWorkout(WorkoutDetails c) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "Name = '" + c.name + "' AND Distance = '" + c.distance + "' AND Duration = '" + c.duration + "' AND Speed = '" + c.speed + "'";
        // Returns the number of affected rows. 0 means no rows were deleted.
        return db.delete(WORKOUTS_TABLE_NAME, whereClause, null);
    }
}
