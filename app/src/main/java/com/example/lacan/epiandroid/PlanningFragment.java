package com.example.lacan.epiandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PlanningFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private TextView period = null;
    private String _session = null;
    private Date start = null;
    private Date end = null;
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    private TextView waitView = null;
    private String infos = null;

    public static PlanningFragment newInstance(String session) {
        PlanningFragment pf = new PlanningFragment();
        pf._session = session;
        return (pf);
    }

    //récupérer  les dates du Lundi au dimanche de chaque semaine et afficher les activités correspondantes
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.planning_layout, container, false);
        period = (TextView) rootview.findViewById(R.id.periode);
        setTime();
        //récuperer les infos de /planning GET
        getWeekActivity();
        return rootview;
    }

    private void getWeekActivity() {
        String startToSend = formater.format(this.start);
        String endToSend = formater.format(this.end);
        System.out.println("date de départ" + startToSend);
        System.out.println("date de de fin" + endToSend);
        new ConnexionTask(this, ConnexionTask.GET, ConnexionTask.ARRAY).execute("3", "planning", "token", this._session,
                "start", startToSend, "end", endToSend);

    }

    @Override
    public void onBackgroundTaskCompleted(String s, int typ) throws JSONException {
        this.infos = s;
        manage_hostReturn(s);
    }

    private void manage_hostReturn(String infos)
    {
        if (this.infos.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnécté du serveur");
        }
        else
        {

            try
            {
                JSonContainer cont = new JSonContainer();
                JSONArray ar = cont.get_array(infos);
                JSONObject obj = null;
                String titre = null;
                int i =  ar.length() - 1;
                while (i >= 0)
                {
                    obj = ar.getJSONObject(i);
                    //si on est inscrit à l'activité
                    if (obj.getString("event_registered") != "null")
                    {
                       System.out.println("Nom activité : " + obj.getString("acti_title"));
                    }
                    i--;
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        setDataToView();
        }
    }

    //set la date du début et de la fin de la semaine en cours
    public void setTime() {
        this.start = new Date();
        //récupère le jour de la semaine au format long
        SimpleDateFormat formate = new SimpleDateFormat("EEEE");
        Calendar cale = Calendar.getInstance();
        //initialise le calendier a la date courrante
        cale.setTime(this.start);

        //tant que le jour n'est pas Lundi
        while (formate.format(this.start).compareTo("Monday") != 0)
        {
            //on retire des jours au calendrier
            cale.add(Calendar.DATE, -1);
            //on set la date avec le calendrier
            this.start = cale.getTime();
            System.out.println("WTF");
        }
        this.end = new Date();
        cale.setTime(this.end);
        while (formate.format(this.end).compareTo("Sunday") != 0)
        {
            //on ajoute des jours au calendrier
            cale.add(Calendar.DATE, 1);
            //on set la date avec le calendrier
            this.end = cale.getTime();
            System.out.println("WTF2");
        }
    }

    public void setDataToView()
    {
        RelativeLayout tmp;

        this.formater.applyPattern("dd-MM-yyyy");
        String startToSend = formater.format(this.start);
        String endToSend = formater.format(this.end);
        System.out.println("AFFICHAGE !!");
        tmp = ((RelativeLayout)waitView.getParent());
        tmp.removeView(waitView);
       this.period.setText("Du Lundi " + startToSend +  "\nAu Lundi " + endToSend);
    }
}
