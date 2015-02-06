package com.example.lacan.epiandroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AccueilFragment extends Fragment implements MyActivity, MyActivityWithBMP {
    private View rootview = null;
    private String _session = null;
    private String infoUser = null;
    private Bitmap profilPicture = null;
    private String login = null;
    private TextView waitView = null;
    private TextView logView = null;
    private ImageView pictureView = null;
    private ListView listView = null;
    private String log = null;
    private ListView listMsg = null;

    //permet d'envoyer des données à l'initialisation du fragment
    public static AccueilFragment newInstance(String session) {
        AccueilFragment af = new AccueilFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.accueil_layout, container, false);
        if (this._session == null)
            this._session = ((WelcomeActivity)getActivity()).getSession();
        System.out.println("Fragment accueil : Session : " + this._session);
        waitView = (TextView) rootview.findViewById(R.id.wait);
        logView = (TextView) rootview.findViewById(R.id.logTime);
        pictureView = (ImageView) rootview.findViewById(R.id.userPicture);
        listMsg = (ListView) rootview.findViewById(R.id.listMsg);
        getInfosAccueil();
        return rootview;
    }

    public void getInfosAccueil() {
        if (this._session != null)
        {
            new ConnexionTask(this, ConnexionTask.POST, ConnexionTask.OBJECT).execute("1", "infos", "token", this._session);
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("1", "messages", "token", this._session);
        }
    }

    public void onBackgroundTaskCompleted(String infos, int type) {
        //this.infoUser = infos;
        System.out.println("page accueil retour telechargement : " + infos);
        manage_hostReturn(infos, type);
    }

    public void onBackgroundTaskCompleted(Bitmap img) {
        this.profilPicture = img;
        setDataToView();
    }


    private void manage_hostReturn(String infos, int type) {
        /*if (this.infoUser.compareTo("io exception") == 0) {
            System.out.println("Vous êtes déconnécté du serveur");
        } */
        if (infos.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnécté du serveur");
        }
        else
        {
            JSonContainer cont = new JSonContainer();
            JSONObject obj = null;
            try
            {
                if (type == ConnexionTask.OBJECT)
                {
                    obj = cont.get_next_valueObj(infos);
                    JSONObject obj2 = obj.getJSONObject("current");
                    this.log = obj2.getString("active_log");
                    System.out.println("log :" + this.log);
                    obj2 = obj.getJSONObject("infos");
                    this.login = obj2.getString("login");
                    new DownloadImgTask(this).execute("https://cdn.local.epitech.eu/userprofil/profilview/" + this.login + ".jpg");
                }
                else
                {
                    JSONArray arr = cont.get_array(infos);
                    JSONObject obj2;
                    String line;
                    List<Spanned> values = new LinkedList<Spanned>();
                    int i = 0;
                    while (i < arr.length())
                    {
                        obj = arr.getJSONObject(i);
                        line = "title: " + obj.getString("title") + "<br/>";

                        obj2 = obj.getJSONObject("user");

                        line += "user: " + obj2.getString("title") + "<br/>";
                        // on fait ce qu'on doit avec obj2.getString("url");

                        line += "content: " + obj.getString("content") + "<br/>";
                        line += "date: " + obj.getString("date") + "<br/>";
                        values.add(Html.fromHtml(line));
                        i++;
                    }
                    ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    listMsg.setAdapter(adapter);
                }

                }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDataToView()
    {
        ((RelativeLayout)waitView.getParent()).removeView(waitView);
        this.logView.setText("Temps de log ces 7 derniers jours : " + this.log);
        this.pictureView.setImageBitmap(this.profilPicture);
    }
}