package com.example.lacan.epiandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class TokenFragment extends Fragment implements MyActivity {
    private View rootview = null;
    private TextView period = null;
    private String _session = null;
    private ListView listAct = null;
    private List<JSONObject> listActInfos;
    private EditText editToken;
    private Date start = null;
    private Date end = null;
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    private TextView waitView = null;
    private String infos = null;
    private int indexActivity = 0;
    private TokenFragment _this = this; // pour le onClickListener, on ne peut pas passer this.

    public static TokenFragment newInstance(String session) {
        TokenFragment pf = new TokenFragment();
        pf._session = session;
        return (pf);
    }

    //récupérer  les dates du Lundi au dimanche de chaque semaine et afficher les activités correspondantes
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_token, container, false);
        period = (TextView) rootview.findViewById(R.id.periode);
        waitView = (TextView) rootview.findViewById(R.id.wait);
        listAct = (ListView) rootview.findViewById(R.id.listActivity);
        editToken = (EditText) rootview.findViewById(R.id.editToken);
        listActInfos = new LinkedList<JSONObject>();
        setTime();
        //récuperer les infos de /planning GET
        getWeekActivity();
        rootview.findViewById(R.id.validateToken).setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (editToken.length() == 8)
                {
                    JSONObject obj = listActInfos.get(indexActivity);

                    try
                    {
                        new ConnexionTask(_this, ConnexionTask.POST, ConnexionTask.OBJECT).execute("4", "token",
                                "token", _session,
                                "scolaryear", obj.getString("scolaryear"),
                                "codemodule", obj.getString("codemodule"),
                                "codeinstance", obj.getString("codeinstance"),
                                "codeacti", obj.getString("codeacti"),
                                "codeevent", obj.getString("codeevent"),
                                "tokenvalidationcode", editToken.getText().toString());
                    }
                    catch (JSONException e)
                    {
                        System.out.println("exception lors de sentToken: " + e.getMessage());
                    }
                }
            }
        });
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
    public void onBackgroundTaskCompleted(String s, int typ) throws JSONException
    {
        if (typ == ConnexionTask.ARRAY)
            manage_hostReturn(s);
        else
        {
            editToken.setText("");
            System.out.println("retour token: " + s);
        }
    }

    private void manage_hostReturn(String infos)
    {
        if (infos.compareTo("io exception") == 0)
        {
            System.out.println("Vous êtes déconnécté du serveur");
        }
        else
        {
            String line;
            try
            {
                JSonContainer cont = new JSonContainer();
                JSONArray ar = cont.get_array(infos);
                JSONObject obj = null;
                List<Spanned> values = new LinkedList<Spanned>();
                int i =  ar.length() - 1;
                while (i >= 0)
                {
                    obj = ar.getJSONObject(i);
                    //si on est inscrit à l'activité
                    if (obj.getString("event_registered") != "null" && obj.getString("allow_token") == "true")
                    {
                        line = "activité: " + obj.getString("acti_title") + "<br/>";
                        line += "start: " + obj.getString("start") + "<br/>";
                        line += "end: " + obj.getString("end") + "<br/>";
                        System.out.println("Nom activité : " + obj.getString("acti_title"));
                        listActInfos.add(obj);
                        values.add(Html.fromHtml(line));
                    }
                    i--;
                }
                ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
                listAct.setAdapter(adapter);
                listAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                                            View arg1, int position, long arg3)
                    {
                        try
                        {
                            indexActivity = position;
                            editToken.setText("");
                            editToken.setHint("valider " + listActInfos.get(position).getString("acti_title"));
                        }
                        catch (JSONException e)
                        {

                        }
                    }
                });
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
        this.end = new Date();
        //récupère le jour de la semaine au format long
        SimpleDateFormat formate = new SimpleDateFormat("EEEE");
        Calendar cale = Calendar.getInstance();
        //initialise le calendier a la date courrante
        cale.setTime(this.end);
        //on retire des jours au calendrier
        cale.add(Calendar.DATE, -2);
        // on set la date avec le calendrier
        this.start = cale.getTime();
    }

    public void setDataToView()
    {
        RelativeLayout tmp;

        System.out.println("AFFICHAGE !!");
        tmp = ((RelativeLayout)waitView.getParent());
        tmp.removeView(waitView);
        this.period.setText("Les activités devant être validées sont:");
    }
}
