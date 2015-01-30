package com.example.lacan.epiandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSonContainer {
        private JSONObject obj = null;
        private JSONArray ar = null;
        private String value = null;
        private JSONTokener tok = null;

    public JSONObject get_next_valueObj(String serializable){
        try {
            tok = new JSONTokener(serializable);
            this.obj = (JSONObject) tok.nextValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (this.obj);
    }
    public JSONObject get_next_valueObj()
    {
        try
        {
            return ((JSONObject) tok.nextValue());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return ((JSONObject) JSONObject.NULL);
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
