package com.example.lacan.epiandroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.util.Iterator;

public class AccueilFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private String _session = null;
    private String infoUser = null;
    private Bitmap profilPicture = null;
    private String login = null;
    private TextView logView = null;
    private ImageView pictureView = null;
    private String log = null;

    //permet d'envoyer des données à l'initialisation du fragment
    public static AccueilFragment newInstance(String session) {
        AccueilFragment af = new AccueilFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.accueil_layout, container, false);
        System.out.println("Fragment accueil : Session : " + this._session);
        logView = (TextView) rootview.findViewById(R.id.logTime);
        pictureView = (ImageView) rootview.findViewById(R.id.userPicture);
        getInfosAccueil();
        return rootview;
    }

    public void getInfosAccueil() {
        if (this._session != null) {
            new ConnexionTask(this).execute("1", "infos", "token", this._session);
        }
    }

    public void onBackgroundTaskCompleted(String infos) {
        this.infoUser = infos;
        System.out.println("page accueil retour telechargement : " + infos);
        manage_hostReturn();
    }

    public void onBackgroundTaskCompleted(Bitmap img) {
        this.profilPicture = img;
        setDataToView();
    }


    private void manage_hostReturn() {
        if (this.infoUser.compareTo("io exception") == 0) {
            System.out.println("Vous êtes déconnécté du serveur");
        } else {
            JSonContainer cont = new JSonContainer();
            JSONObject obj = null;
            obj = cont.get_next_valueObj(this.infoUser);
            try
            {
                JSONObject obj2 = obj.getJSONObject("current");
                this.log = obj2.getString("active_log");
                System.out.println("log :" + this.log);
                obj2 = obj.getJSONObject("infos");
                this.login = obj2.getString("login");
                new DownloadImgTask(this).execute("http://cdn.local.epitech.eu/userprofil/" + this.login + ".bmp" );


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public void setDataToView()
    {
        this.logView.setText("Temps de log ces 7 derniers jours : " + this.log);
        this.pictureView.setImageBitmap(this.profilPicture);
    }
}

