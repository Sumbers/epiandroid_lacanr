package com.example.lacan.epiandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSonContainer {
        private JSONObject obj = null;
        private JSONArray ar = null;
        private String value = null;

        JSonContainer(String serializable) throws JSONException {
            this.obj = (JSONObject) new JSONTokener(serializable).nextValue();
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
