package com.example.adam.activitytracker;

/**
 * Created by Adam on 20/03/2015.
 */

// class and its constructor
public class WorkoutDetails {
    public String name = "";
    public String distance = "";
    public String duration = "";
    public String speed = "";

    WorkoutDetails(String n, String a, String b, String c){
        name = n;
        distance= a;
        duration = b;
        speed = c;
    }
}
