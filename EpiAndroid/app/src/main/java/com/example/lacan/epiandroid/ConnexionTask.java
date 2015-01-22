package com.example.lacan.epiandroid;


import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ConnexionTask extends AsyncTask<String, Integer, String>
{
    ConnexionActivity _obj;

    ConnexionTask(ConnexionActivity obj)
    {
        this._obj = obj;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            URI serv = new URI("https://epitech-api.herokuapp.com/login");
            HttpPost httppost = new HttpPost(serv);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("login", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpresponse = httpclient.execute(httppost);
            publishProgress(50);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
            publishProgress(100);
            return (reader.readLine());
        }
        catch (URISyntaxException e) {
            return ("syntax problem");
        } catch (ClientProtocolException e) {
            return ( "protocole exception " + e.getMessage());
        } catch (IOException e) {
            return ("io exception");
        }
            }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            this._obj.onBackgroundTaskCompleted(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
