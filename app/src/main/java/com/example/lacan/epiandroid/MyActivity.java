package com.example.lacan.epiandroid;


import org.json.JSONException;

public interface MyActivity {
    public void onBackgroundTaskCompleted(String s, int type) throws JSONException;
}