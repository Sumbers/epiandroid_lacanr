package com.example.lacan.epiandroid;


import android.os.AsyncTask;
import android.provider.MediaStore;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ConnexionTask extends AsyncTask<String, Integer, String>
{
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int DELETE = 2;

    MyActivity _obj = null;
    private int request;
    ConnexionTask(MyActivity obj, int req)
    {
        this._obj = obj;
        this.request = req;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        try
        {
            int nb;

            nb = Integer.parseInt(params[0]);
            HttpResponse httpresponse;
           switch (this.request)
           {
               case GET:
                   httpresponse = doGet(nb, httpclient, params);
                   break;
               case POST:
                   httpresponse = doPost(nb, httpclient, params);
                   break;
               case DELETE:
                   httpresponse = doDelete(nb, httpclient, params);
                   break;
               default:
                   return "";
           }
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
            System.out.println("page accueil retour telechargement readerlaca : " + reader);
            String s;
            String response = null;
            response = reader.readLine();
            while ((s = reader.readLine()) != null)
                response += s;
            return (response);
        }
        catch (URISyntaxException e) {
            return ("syntax problem");
        }
        catch (ClientProtocolException e) {
            return ("protocole exception " + e.getMessage());
        }
        catch (IOException e) {
            return ("io exception");
        }
    }

    protected HttpResponse doPost(int nb, HttpClient httpclient, String... params) throws UnsupportedEncodingException, IOException, URISyntaxException
    {
        URI serv = new URI("https://epitech-api.herokuapp.com/" + params[1]);
        HttpPost httppost = new HttpPost(serv);

        int max = 2 + nb * 2;
        int i = 2;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(nb);
        while (i < max)
        {
            nameValuePairs.add(new BasicNameValuePair(params[i], params[i + 1]));
            i = i + 2;
        }
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return (httpclient.execute(httppost));
    }

    protected HttpResponse doGet(int nb, HttpClient httpclient, String... params) throws UnsupportedEncodingException, IOException, URISyntaxException
    {
        String url = "https://epitech-api.herokuapp.com/" + params[1];
        if (nb > 0)
        {
            url += "?" + params[2] + "=" + params[3];
        }
        int max = 2 + nb * 2;
        int i = 4;

        while (i < max)
        {
            url += "&" + params[i] + "=" + params[i + 1];
            i = i + 2;
        }
        URI serv = new URI(url);
        HttpGet httppost = new HttpGet(serv);
        return (httpclient.execute(httppost));
    }

    protected HttpResponse doDelete(int nb, HttpClient httpclient, String... params) throws UnsupportedEncodingException, IOException, URISyntaxException
    {
        String url = "https://epitech-api.herokuapp.com/" + params[1];
        if (nb > 0)
        {
            url += "?" + params[2] + "=" + params[3];
        }
        int max = 2 + nb * 2;
        int i = 4;

        while (i < max)
        {
            url += "&" + params[i] + "=" + params[i + 1];
            i = i + 2;
        }
        URI serv = new URI(url);
        HttpDelete httppost = new HttpDelete(serv);
        return (httpclient.execute(httppost));
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
