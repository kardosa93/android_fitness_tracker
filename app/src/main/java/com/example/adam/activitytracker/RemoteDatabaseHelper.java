package com.example.adam.activitytracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Adam on 10/04/2015.
 */
public class RemoteDatabaseHelper {

    private final String rootURL = "http://mayar.abertay.ac.uk/~1206633/sql/";
    private final String insertURL = rootURL + "insert.php";
    private final String getListURL = rootURL + "getlist.php";
    private final String eraseDB = rootURL + "erase.php";
    private static final String[] COLUMN_NAMES = {"Name", "Distance", "Duration", "Speed"};
    private final Context parentActivityContext;

    /* Local storage for workouts list. */
    private ArrayList<WorkoutDetails> contactsOutput = new ArrayList<WorkoutDetails>();

    RemoteDatabaseHelper(Context context) {
        parentActivityContext = context;
    }

    public void addWorkout(WorkoutDetails details) {
        AddWorkoutTask task = new AddWorkoutTask();
        task.execute(details);
    }

    public ArrayList<WorkoutDetails> getWorkoutsList() {
        GetWorkoutTask task = new GetWorkoutTask();
        task.execute();
        return contactsOutput;
    }

    // Asynchronous task for adding a workout to DB. Runs on a background thread.
    private class AddWorkoutTask extends AsyncTask<WorkoutDetails, Void, Void> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivityContext);
        private HttpClient httpClient = new DefaultHttpClient();
        private HttpPost httpPost = new HttpPost(insertURL);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Adding workouts...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This function requires a list of parameters, but we will be using only one [0].
        @Override
        protected Void doInBackground(WorkoutDetails... details) {
            WorkoutDetails d = details[0];

            /* Add POST parameters to list. */
            ArrayList<NameValuePair> workoutDetails = new ArrayList<NameValuePair>(4); // note the size parameter
            workoutDetails.add(new BasicNameValuePair(COLUMN_NAMES[0], d.name));
            workoutDetails.add(new BasicNameValuePair(COLUMN_NAMES[1], d.distance));
            workoutDetails.add(new BasicNameValuePair(COLUMN_NAMES[2], d.duration));
            workoutDetails.add(new BasicNameValuePair(COLUMN_NAMES[3], d.speed));

            /* Encode POST data. */
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(workoutDetails));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /* Finally, make an HTTP POST request to server. */
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                // Write response to log
                Log.i("Response:", responseString);
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
            return null; // must be here if return type is Void
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(Void result) {
            // This is where we would process the results if needed.
            this.progressDialog.dismiss();
        }
    }

    // Asynchronous task for getting the list of workouts from the remote DB. Runs on a background thread.
    private class GetWorkoutTask extends AsyncTask<Void, Void, ArrayList<WorkoutDetails>> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivityContext);
        private HttpClient httpClient = new DefaultHttpClient();
        private HttpGet httpGet = new HttpGet(getListURL);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Retrieving workouts list...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This time we don't need to pass any parameters to the task, hence using Void.
        @Override
        protected ArrayList<WorkoutDetails> doInBackground(Void... params) {

           /* Make an HTTP request. */
            HttpResponse response = null;
            String responseString = "";
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, "UTF-8");

                // Write response to log for debugging.
                Log.i("Response:", responseString);
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            // Process the results in background.
            JSONArray contactsArray = null;
            if (responseString != null && responseString != "") {
                try {
                    contactsArray = new JSONArray(responseString);
                } catch (JSONException e) {
                    // Log exceptions
                    e.printStackTrace();
                }
            }

            /* Create and ArrayList to hold workouts. */
            ArrayList<WorkoutDetails> details = new ArrayList<WorkoutDetails>();

            if (contactsArray != null) {
                /* Go through the list of workouts, and convert them to WorkoutDetails objects, putting
                 those into an ArrayList. */
                for (int i = 0; i < contactsArray.length(); i++) {
                    try {
                        JSONObject contactEntry = contactsArray.getJSONObject(i);
                        String name = contactEntry.get(COLUMN_NAMES[0]).toString();
                        String dist = contactEntry.get(COLUMN_NAMES[1]).toString();
                        String dur = contactEntry.get(COLUMN_NAMES[2]).toString();
                        String speed = contactEntry.get(COLUMN_NAMES[3]).toString();
                        // Log for debugging.
                        Log.i("Response Workout:", "" + name + ", " + dist + ", " + dur + ", " + speed);
                       details.add(new WorkoutDetails(name, dist, dur, speed));
                    } catch (JSONException e) {
                        // Log exceptions
                        e.printStackTrace();
                    }
                }
            }
            /* Return the list of workokuts. */
            return details;
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(ArrayList<WorkoutDetails> result) {
            contactsOutput = result;
            this.progressDialog.dismiss();
        }
    }
}
