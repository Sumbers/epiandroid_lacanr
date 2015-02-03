package com.example.lacan.epiandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSonContainer {
        private JSONObject obj = null;
        private JSONArray ar = null;
        private String value = null;

    public JSONObject get_next_valueObj(String serializable){
        try {
            this.obj = (JSONObject) new JSONTokener(serializable).nextValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (this.obj);
    }

    public JSONArray get_array(String serializable)
    {
        try {
            return (JSONArray) new JSONTokener(serializable).nextValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (null);
    }

    public JSONObject get_object(String serializable)
    {
        try {
            return (JSONObject) new JSONTokener(serializable).nextValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (null);
    }

    public String getStringfromKey(String key)
    {
        try {
            this.value = this.obj.getString(key);
            return (this.value);
        }
        catch (JSONException e)
        {
            return (null);
        }
    }
}
