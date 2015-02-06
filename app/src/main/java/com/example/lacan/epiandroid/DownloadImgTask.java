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
import java.io.FilterInputStream;
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
    MyActivityWithBMP _obj;

    public DownloadImgTask(MyActivityWithBMP frag) {
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
    protected void onPostExecute(Bitmap img) {
        if (img != null) {
            this._obj.onBackgroundTaskCompleted(img);
        }
        else
            System.out.println("Image null");
    }

    public Bitmap downloadBitmap(String link) {
        Bitmap img = null;
        System.out.println("Lien : " + link);
        try {
            InputStream in = new java.net.URL(link).openStream();
            img = BitmapFactory.decodeStream(new FlushedInputStream(in));
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return img;
    }

    static class FlushedInputStream extends FilterInputStream {

        /**
         * The constructor that takes in the InputStream reference.
         *
         * @param inputStream the input stream reference.
         */
        public FlushedInputStream(final InputStream inputStream) {
            super(inputStream);
        }

        /**
         * Overriding the skip method to actually skip n bytes.
         * This implementation makes sure that we actually skip
         * the n bytes no matter what.
         * {@inheritDoc}
         */
        @Override
        public long skip(final long n) throws IOException {
            long totalBytesSkipped = 0L;
            //If totalBytesSkipped is equal to the required number
            //of bytes to be skipped i.e. "n"
            //then come out of the loop.
            while (totalBytesSkipped < n) {
                //Skipping the left out bytes.
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                //If number of bytes skipped is zero then
                //we need to check if we have reached the EOF
                if (bytesSkipped == 0L) {
                    //Reading the next byte to find out whether we have reached EOF.
                    int bytesRead = read();
                    //If bytes read count is less than zero (-1) we have reached EOF.
                    //Cant skip any more bytes.
                    if (bytesRead < 0) {
                        break;  // we reached EOF
                    } else {
                        //Since we read one byte we have actually
                        //skipped that byte hence bytesSkipped = 1
                        bytesSkipped = 1; // we read one byte
                    }
                }
                //Adding the bytesSkipped to totalBytesSkipped
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
