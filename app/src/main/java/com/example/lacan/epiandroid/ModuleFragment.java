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

public class ModuleFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private String _session = null;
    private ListView listModules = null;
    private String _infos = null;
    private String _infosUser = null;
    private User me = null;
    private TextView wait = null;
    private int filtreNb = 0;

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
        wait = (TextView) rootview.findViewById(R.id.wait);
        getModules();
        rootview.findViewById(R.id.allModules).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                filtreNb = 0;
                manage_module();
            }
        });
        rootview.findViewById(R.id.myModule).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                filtreNb = 1;;
                manage_module();
            }
        });
        return rootview;
    }

    public void getModules() {
        if (this._session != null)
        {
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("1", "modules", "token", this._session);
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.OBJECT).execute("1", "infos", "token", this._session);
        }
    }

    public void onBackgroundTaskCompleted(String infos, int type) {
        if (type == ConnexionTask.ARRAY)
        {
            this._infos = infos;
            System.out.println("page modules retour telechargement : " + infos);
            manage_moduleReturn();
        }
        else
        {
            this._infosUser = infos;
            manage_userReturn();
        }
    }

    private void manage_userReturn()
    {
        if (_infosUser.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnecté du serveur");
        }
        else
        {
            manage_user();
        }
    }
    private void manage_moduleReturn()
    {
        if (_infos.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnecté du serveur");
        }
        else
        {
            manage_module();
        }
    }

    private void manage_user()
    {
        JSonContainer cont = new JSonContainer();
        JSONObject obj = cont.get_next_valueObj(_infosUser);
        me = new User(obj);
    }

    private void manage_module() {
        JSonContainer cont = new JSonContainer();
        JSONObject obj = null;
        try
        {
                JSONObject tab = cont.get_object(_infos);
                JSONArray arr = tab.getJSONArray("modules");
                List<Spanned> values = new LinkedList<Spanned>();
                int i = 0;
                 String line="";
                while (i < arr.length())
                {
                    obj = arr.getJSONObject(i);
                    switch (filtreNb)
                    {
                        case 0:
                        {
                            line += "Module: " + obj.getString("title") + "<br/>";
                            line += "Date: " + obj.getString("date_ins") + "<br/>";

                            line += "Code: " + obj.getString("codemodule") + "<br/>";
                            line += "Grade: " + obj.getString("grade") + "<br/>";
                            line += "Crédits: " + obj.getString("credits") + "<br/>";
                            line += "Semestre: " + obj.getString("semester") + " (cycle " + obj.getString("cycle") + ") <br/>";
                            values.add(Html.fromHtml(line));
                            line="";
                            break;
                        }
                        case 1:
                        {
                            if (me != null)
                            {
                                String promo = null;
                                if ((promo = me.get_promo()) != null) {
                                    try {
                                        System.out.println("La promo est : " + promo);
                                        if (obj.getString("semester").compareTo(promo) == 0)
                                        {
                                            line += "Module: " + obj.getString("title") + "<br/>";
                                            line += "Date: " + obj.getString("date_ins") + "<br/>";

                                            line += "Code: " + obj.getString("codemodule") + "<br/>";
                                            line += "Grade: " + obj.getString("grade") + "<br/>";
                                            line += "Crédits: " + obj.getString("credits") + "<br/>";
                                            line += "Semestre: " + obj.getString("semester") + " (cycle " + obj.getString("cycle") + ") <br/>";
                                            values.add(Html.fromHtml(line));
                                            line="";
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            break;
                        }
                    }
                    i++;
                }
                wait.setVisibility(View.GONE);
                ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
                listModules.setAdapter(adapter);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}