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

public class SusieFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private String _session = null;
    private ListView listSusies = null;
    private List<Spanned> values;

    //permet d'envoyer des données à l'initialisation du fragment
    public static SusieFragment newInstance(String session) {
        SusieFragment af = new SusieFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_susie, container, false);
        System.out.println("Fragment accueil : Session : " + this._session);
        listSusies = (ListView) rootview.findViewById(R.id.listSusie);
        values = new LinkedList<Spanned>();
        getInfosAccueil();
        return rootview;
    }

    public void getInfosAccueil() {
        if (this._session != null)
        {
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("4", "susies", "token", this._session, "start", "2015-01-26", "end", "2015-02-01", "get", "all");
        }
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
            JSONObject obj = null;
            try
            {
                if (type == ConnexionTask.ARRAY)
                {
                    JSONArray arr = cont.get_array(infos);
                    JSONArray arr2;
                    JSONObject obj2;
                    JSONObject susie;
                    String line;

                    int i = 0;
                    int j;
                    while (i < arr.length())
                    {
                        obj = arr.getJSONObject(i);
                        line = "teacher: ";
                        //line += obj.getString("maker") + "<br/>";
                        susie = new JSonContainer().get_next_valueObj(obj.getString("maker")); //je fais ça parce que le champs maker semble contenir du json
                        line += susie.getString("title") + "<br/>";
                        j = 0;
                        /*while (j < arr2.length())
                        {
                            obj2 = arr2.getJSONObject(j);
                            line += obj2.getString("login");
                            j++;
                            if (j < arr2.length())
                                line += ", ";
                        }*/
                        line += "start: " + obj.getString("start") + "<br/>";
                        line += "end: " + obj.getString("end") + "<br/>";
                        values.add(Html.fromHtml(line));
                        i++;
                    }
                    ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    listSusies.setAdapter(adapter);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}