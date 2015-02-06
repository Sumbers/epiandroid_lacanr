package com.example.lacan.epiandroid;


import org.json.JSONException;
import org.json.JSONObject;

public class User {
    JSONObject _userdata;

    User(JSONObject obj)
    {
            _userdata = obj;
    }

    public String get_promo()
    {
        try
        {
           return(_userdata.getJSONObject("infos").getString("semester"));

        } catch (JSONException e) {
           return (null);
        }
    }
}
