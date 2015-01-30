package com.example.lacan.epiandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
    private Bitmap _img;
    AccueilFragment _obj;

    public DownloadImgTask(AccueilFragment frag) {
        this._obj = frag;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            this._obj.onBackgroundTaskCompleted(this._img);
        }
        System.out.println("Image null");
    }

    static Bitmap downloadBitmap(String link) {
        Bitmap mIcon11 = null;
        System.out.println("Lien : " + link);
        try {
            InputStream in = new java.net.URL(link).openStream();
            mIcon11 = BitmapFactory.decodeStream(new BufferedInputStream(in));
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return mIcon11;
    }
}
