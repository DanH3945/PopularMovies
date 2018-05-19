package com.hereticpurge.popularmovies;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public abstract class VolleyAppCompatActivity extends AppCompatActivity {

    static void volleyQuery(String urlString, VolleyAppCompatActivity volleyActivity) {
        volleyQuery(urlString, null, volleyActivity);
    }

    static void volleyQuery(String urlString, final String requestTag, final VolleyAppCompatActivity volleyActivity) {

        // Volley queue takes the volley network requests and sends them off FIFO
        // takes an activity to allow responses to the correct activity.
        RequestQueue queue = Volley.newRequestQueue(volleyActivity);

        // Specific volley requests
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                urlString,
                new Response.Listener<String>() {

                    @Override
                    synchronized public void onResponse(String jsonString) {
                        // sends a json string to the onResponse method in the calling activity
                        volleyActivity.onResponse(jsonString, requestTag);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // same as above but sends the volley error object for debugging
                        volleyActivity.onErrorResponse(error, requestTag);
                    }
                });
        queue.add(stringRequest);
    }

    abstract void onResponse(String jsonString, String requestTag);

    abstract void onErrorResponse(VolleyError error, String requestTag);
}
