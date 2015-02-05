package com.example.lacan.epiandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class PlanningFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private TextView period = null;
    private String _session = null;
    private Date current = new Date();
    private Date start = null;
    private Date end = null;
    private TextView waitView = null;
    private RadioGroup filtre = null;
    private String infos = null;
    private ListView activityListView = null;
    private SimpleDateFormat formatSent = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatGet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat formatDay = new SimpleDateFormat("EEEE");
    private SimpleDateFormat formatHour = new SimpleDateFormat("HH");
    private SimpleDateFormat formatDisplay = new SimpleDateFormat("dd-MM-yyyy");
    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapteur = null;
    private int nbAct = 0;
    private int filtreNb;

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
        waitView = (TextView) rootview.findViewById(R.id.wait);
        activityListView = (ListView) rootview.findViewById(R.id.listActivity);
        filtre = (RadioGroup) rootview.findViewById(R.id.filtre);
        setTime(0);
        //récuperer les infos de /planning GET
        getWeekActivity();
        rootview.findViewById(R.id.nextWeekButton).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (list != null)
                    list.clear();
                setTime(1);
                getWeekActivity();
            }
    });
        rootview.findViewById(R.id.precWeekButton).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (list != null)
                    list.clear();
                setTime(-1);
                getWeekActivity();
            }
        });
        rootview.findViewById(R.id.register).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("trololo");
                filtreNb = 0;
                if (list != null)
                    list.clear();
                setTime(0);
                getWeekActivity();
            }
        });
        rootview.findViewById(R.id.promo).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("trololo");
                filtreNb = 1;
            }
        });
        rootview.findViewById(R.id.all).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("trololo");
                filtreNb = 2;
                if (list != null)
                    list.clear();
                setTime(0);
                getWeekActivity();
            }
        });
        return rootview;
    }

    private void getWeekActivity() {
        String startToSend = formatSent.format(this.start);
        String endToSend = formatSent.format(this.end);
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
            JSonContainer cont = new JSonContainer();
            JSONArray ar = cont.get_array(infos);
            System.out.println("infos = " + infos );
            switch (filtreNb)
            {
                case 0:
                {
                    Planning plan = new Planning(ar);
                    try {
                        plan.onlyRegistered();
                        if ((list = plan.get_activities()) != null)
                            nbAct = list.size();
                        else
                            nbAct = 0;
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
               case 1:
                {
                    break;
                }
                case 2:
                {
                    Planning plan = new Planning(ar);
                    if ((list = plan.get_activities()) != null)
                        nbAct = list.size();
                    else
                        nbAct = 0;
                    break;
                }
            }
            setDataToView();
        }
    }

    //set la date du début et de la fin de la semaine en cours
    public void setTime(int when) {
        this.start = this.current;
        //récupère le jour de la semaine au format long
        Calendar cale = Calendar.getInstance();
        //initialise le calendier a la date courrante
        cale.setTime(this.start);

        //tant que le jour n'est pas Lundi
        while (formatDay.format(this.start).compareTo("Monday") != 0)
        {
            //on retire des jours au calendrier
            cale.add(Calendar.DATE, -1);
            //on set la date avec le calendrier
            this.start = cale.getTime();
            System.out.println("WTF");
            //si on veut la semaine suivante
        }
        if (when == 1)
        {
            //on ajoute 7 jours au lundi en cours
            cale.add(Calendar.DATE, 7);
            this.start = cale.getTime();
        }
        else if (when == -1)
        {
            //on retire 7 jours au lundi en cours
            cale.add(Calendar.DATE, -7);
            this.start = cale.getTime();
        }
        this.end = this.current;
        cale.setTime(this.end);
        while (formatDay.format(this.end).compareTo("Sunday") != 0)
        {
            //on ajoute des jours au calendrier
            cale.add(Calendar.DATE, 1);
            //on set la date avec le calendrier
            this.end = cale.getTime();
            System.out.println("WTF2");
        }
        if (when == 1)
        {
            //on ajoute 7 jours au dimanche en cours
            cale.add(Calendar.DATE, 7);
            this.end = cale.getTime();
        }
        else if (when == -1)
        {
            //on retire 7 jours au dimanche en cours
            cale.add(Calendar.DATE, -7);
            this.end = cale.getTime();
        }
        this.current = this.start;
    }

    public void setDataToView()
    {
        String startToSend = formatDisplay.format(this.start);
        String endToSend = formatDisplay.format(this.end);
        System.out.println("AFFICHAGE !!");
        if (nbAct > 0)
        {
            waitView.setVisibility(View.GONE);
            activityListView.setVisibility(View.VISIBLE);
            this.period.setText("Du Lundi " + startToSend +  "\nAu Lundi " + endToSend);
            String[] from = {"jour", "activity", "room", "start"};
            int[] to = {R.id.jour, R.id.activity, R.id.room, R.id.hour};
            this.adapteur = new SimpleAdapter(getActivity(), list,
                    R.layout.list_planning_layout, from, to);
            activityListView.setAdapter(this.adapteur);
            nbAct = 0;
        }
        else
        {
            System.out.println("il 'y a rien");
            activityListView.setVisibility(View.GONE);
            this.period.setText("Du Lundi " + startToSend +  "\nAu Lundi " + endToSend);
            waitView.setVisibility(View.VISIBLE);
            waitView.setText("Rien à afficher");
        }
    }
}
