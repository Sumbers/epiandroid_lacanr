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

public class NotesFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private String _session = null;
    private ListView listNotes = null;

    //permet d'envoyer des données à l'initialisation du fragment
    public static NotesFragment newInstance(String session) {
        NotesFragment af = new NotesFragment();
        af._session = session;
        return (af);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_notes, container, false);
        System.out.println("Fragment notes : Session : " + this._session);
        listNotes = (ListView) rootview.findViewById(R.id.listNotes);
        getNotes();
        return rootview;
    }

    public void getNotes() {
        System.out.println("Allez l'om");
        if (this._session != null)
        {
            new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("1", "marks", "token", this._session);
        }
    }

    public void onBackgroundTaskCompleted(String infos, int type) {
        //this.infoUser = infos;
        System.out.println("page notes retour telechargement : " + infos);
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
                    JSONArray arr = tab.getJSONArray("notes");
                    String line;
                    List<Spanned> values = new LinkedList<Spanned>();
                    int i = 0;
                    while (i < arr.length())
                    {
                        obj = arr.getJSONObject(i);
                        line = "Module: " + obj.getString("titlemodule") + "<br/>";

                        line += "Projet: " + obj.getString("title") + "<br/>";
                        line += "Date: " + obj.getString("date") + "<br/>";
                        line += "Note: " + obj.getString("final_note") + "<br/>";
                        line += "Commentaire: " + obj.getString("comment") + "<br/>";
                        values.add(Html.fromHtml(line));
                        i++;
                    }
                    ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    listNotes.setAdapter(adapter);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}