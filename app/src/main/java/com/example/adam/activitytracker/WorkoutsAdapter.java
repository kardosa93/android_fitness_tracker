package com.example.adam.activitytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 22/03/2015.
 */

public class WorkoutsAdapter extends ArrayAdapter<WorkoutDetails> {
    public WorkoutsAdapter(Context context, ArrayList<WorkoutDetails> workoutses){
        super(context, 0, workoutses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* Get the workouts item for this position. */
        WorkoutDetails workouts = getItem(position);
        /* Check if an existing view is being reused, otherwise inflate the view. */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_layout, parent, false);
        }
        /* Lookup view for data population. */
        TextView display_name = (TextView) convertView.findViewById(R.id.display_name);
        TextView display_distance = (TextView) convertView.findViewById(R.id.display_distance);
        TextView display_duration = (TextView) convertView.findViewById(R.id.display_duration);
        TextView display_speed = (TextView) convertView.findViewById(R.id.display_speed);
        /* Populate the data into the template view. */
        display_name.setText(workouts.name);
        display_distance.setText(workouts.distance);
        display_duration.setText(workouts.duration);
        display_speed.setText(workouts.speed);
        /* Return the completed view to render on screen. */
        return convertView;
    }
}
