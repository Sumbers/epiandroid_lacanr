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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class TrombiFragment extends Fragment implements MyActivity, MyActivityWithBMP {
    private View rootview = null;
    private String _session = null;
    private ListView listSusies = null;
    private List<Spanned> values;
    private TextView infosUser = null;
    private ImageView pictureView = null;
    private EditText loginField = null;
    private Button checkButton = null;
    //permet d'envoyer des données à l'initialisation du fragment
    public static TrombiFragment newInstance(String session) {
        TrombiFragment af = new TrombiFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_trombi, container, false);
        System.out.println("Fragment accueil : Session : " + this._session);
        listSusies = (ListView) rootview.findViewById(R.id.listSusie);
        values = new LinkedList<Spanned>();
        infosUser = (TextView) rootview.findViewById(R.id.infosUser);
        pictureView = (ImageView) rootview.findViewById(R.id.userPicture);
        loginField = (EditText) rootview.findViewById(R.id.loginField);
        checkButton = (Button) rootview.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getInfos();
            }
        });
        return rootview;
    }

    public void getInfos()
    {
        if (this._session != null && !loginField.getText().toString().isEmpty())
        {
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.OBJECT).execute("2", "user", "token", this._session, "user", loginField.getText().toString());
            new DownloadImgTask(this).execute("https://cdn.local.epitech.eu/userprofil/profilview/" + loginField.getText().toString() + ".jpg");
        }
    }

    public void onBackgroundTaskCompleted(Bitmap img)
    {
        this.pictureView.setImageBitmap(img);
    }

    public void onBackgroundTaskCompleted(String infos, int type) {
        //this.infoUser = infos;
        System.out.println("page accueil retour telechargement : " + infos);
        manage_hostReturn(infos, type);
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
            try
            {
                if (type == ConnexionTask.OBJECT)
                {
                    JSONObject obj = cont.get_next_valueObj(infos);
                    JSONObject objNS = obj.getJSONObject("nsstat");
                    JSONObject objGpa = obj.getJSONArray("gpa").getJSONObject(0);
                    infosUser.setText("active log time: " + objNS.getString("active") + "\n"
                            + "crédits: " + obj.getString("credits") + "\n"
                            + "GPA: " + objGpa.getString("gpa"));
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}