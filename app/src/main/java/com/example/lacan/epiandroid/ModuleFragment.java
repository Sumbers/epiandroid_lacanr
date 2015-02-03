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

public class ModuleFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private String _session = null;
    private ListView listModules = null;

    //permet d'envoyer des données à l'initialisation du fragment
    public static ModuleFragment newInstance(String session) {
        ModuleFragment af = new ModuleFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_module, container, false);
        System.out.println("Fragment notes : Session : " + this._session);
        listModules = (ListView) rootview.findViewById(R.id.listModules);
        getModules();
        return rootview;
    }

    public void getModules() {
        if (this._session != null)
        {
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("1", "modules", "token", this._session);
        }
    }

    public void onBackgroundTaskCompleted(String infos, int type) {
        //this.infoUser = infos;
        System.out.println("page modules retour telechargement : " + infos);
        manage_hostReturn(infos, type);
    }

    private void manage_hostReturn(String infos, int type) {
        /*if (this.infoUser.compareTo("io exception") == 0) {
            System.out.println("Vous êtes déconnécté du serveur");
        } */
        System.out.println("Test : " + infos);
        if (infos.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnecté du serveur");
        }
        else
        {
            JSonContainer cont = new JSonContainer();
            JSONObject obj = null;
            try
            {
                if (type == ConnexionTask.ARRAY)
                {
                    JSONObject tab = cont.get_object(infos);
                    JSONArray arr = tab.getJSONArray("modules");
                    String line;
                    List<Spanned> values = new LinkedList<Spanned>();
                    int i = 0;
                    while (i < arr.length())
                    {
                        obj = arr.getJSONObject(i);
                        line = "Module: " + obj.getString("title") + "<br/>";
                        line += "Date: " + obj.getString("date_ins") + "<br/>";

                        line += "Code: " + obj.getString("codemodule") + "<br/>";
                        line += "Grade: " + obj.getString("grade") + "<br/>";
                        line += "Crédits: " + obj.getString("credits") + "<br/>";
                        line += "Semestre: " + obj.getString("semester") + " (cycle " + obj.getString("cycle") + ") <br/>";

                        values.add(Html.fromHtml(line));
                        i++;
                    }
                    ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    listModules.setAdapter(adapter);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}