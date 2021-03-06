package com.example.lacan.epiandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Sumbers on 05/02/2015.
 */
public class Planning
{
    private List<JSONObject> _obj = null;
    private List<JSONObject> _objOnly = null;
    private List<JSONObject> _objPromo = null;
    private List<JSONObject> _objModules = null;
    private List<HashMap<String, String>> _activities = null;
    private int _nbAct = 0;
    private SimpleDateFormat formatSent = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatGet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat formatDay = new SimpleDateFormat("EEEE");
    private SimpleDateFormat formatHour = new SimpleDateFormat("HH");
    private SimpleDateFormat formatDisplay = new SimpleDateFormat("dd-MM-yyyy");
    private Map date_equivalent = new Hashtable();

    Planning(JSONArray ar){
       if (ar.length() > 0) {
           _nbAct = ar.length();
           _obj = new ArrayList<JSONObject>(_nbAct);
       }
        //int goodTitle = 1;
        int indexList = 0;
        for (int i = _nbAct - 1;i >= 0;i--)
        {
            try {
                _obj.add(indexList, ar.getJSONObject(i));
                indexList++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("il y a " + _nbAct + " activités cette semaine");
        setequiv();
        try {
            setActivities(_nbAct, _obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onlyMyModules() throws JSONException {
        int only = 0;
        //on compte le nombre d'event ou on est enregistré
        for (int i = _nbAct - 1; i >= 0;i--)
        {
            try {
                if (_obj.get(i).getString("module_registered").compareTo("true") == 0)
                    only++;
            }
            catch (JSONException e)
            {
                //on ne fait pas only++
            }
        }
        if (only == 0)
        {
            _activities = null;
            return;
        }
        else
        {
            System.out.println("dans mes modules il y a " + only + " activités");
            _objModules = new ArrayList<JSONObject>(only);
            int indexList = 0;
            //on récupère un objet pour chaque event
            for (int i = _nbAct - 1; i >= 0; i--) {
                try
                {
                    if (_obj.get(i).getString("module_registered").compareTo("true") == 0) {
                        _objModules.add(indexList, _obj.get(i));
                        indexList++;
                    }
                }
                catch (JSONException e)
                {
                    //on ne fait rien
                }
            }
            setActivities(indexList, _objModules);
        }
    }

    public void onlyPromo(String promo) throws JSONException {
        int only = 0;
        //on compte le nombre d'event ou la promo a des activités
        for (int i = _nbAct - 1; i >= 0;i--)
        {
            String sem = null;
            try {
                if (((sem = _obj.get(i).getString("semester")) != "null") &&
                        (sem.compareTo(promo) == 0))
                    only++;
            }
            catch (JSONException E)
            {
                //on ne fait pas only++;
            }
        }
        if (only == 0)
        {
            _activities = null;
            return;
        }
        else
        {
            System.out.println("la promo a " + only + " activités");
            _objPromo = new ArrayList<JSONObject>(only);
            int indexList = 0;
            //on récupère un objet pour chaque event
            for (int i = _nbAct - 1; i >= 0; i--)
            {
                String sem = null;
                try
                {
                    if (((sem = _obj.get(i).getString("semester")) != "null") &&
                            (sem.compareTo(promo) == 0)) {
                        _objPromo.add(indexList, _obj.get(i));
                        indexList++;
                    }
                }
                catch (Exception e)
                {
                    //on ne fait rien
                }
            }
            setActivities(only, _objPromo);
        }
    }

    public void onlyRegistered() throws JSONException {
    int only = 0;
    //on compte le nombre d'event ou on est enregistré
    for (int i = _nbAct - 1; i >= 0;i--)
    {
        if (_obj.get(i).getString("event_registered") != "null")
            only++;
    }
    if (only == 0)
    {
        _activities = null;
        return;
    }
    else
    {
        System.out.println("je ne suis inscrit qu'a " + only + " activités");
        _objOnly = new ArrayList<JSONObject>(only);
        int indexList = 0;
        //on récupère un objet pour chaque event
        for (int i = _nbAct - 1; i >= 0; i--) {
            if (_obj.get(i).getString("event_registered") != "null") {
                _objOnly.add(indexList, _obj.get(i));
                indexList++;
            }
        }
        setActivities(indexList, _objOnly);
    }
}

    private void setActivities(int indexList, List<JSONObject> objOnly) throws JSONException {
        _activities = new ArrayList<HashMap<String, String>>(indexList);
        String jourPrec = " ";
        for (int i = 0; i < objOnly.size();i++)
        {
            String activity = null;
            String jour = null;
            String room = null;
            String hour = null;
            if ((jour = getActivityDayInFrench(objOnly.get(i).getString("start"))) != null)
            {
                HashMap<String, String> map = new HashMap<String, String>();
                if (jour.compareTo(jourPrec) != 0)
                {
                    jourPrec = jour;
                    map.put("jour", jour);
                }
                try
                {
                   activity = objOnly.get(i).getString("acti_title");
                   map.put("activity", activity);
                }
                catch (Exception e)
                {
                    map.put("activity", objOnly.get(i).getString("title"));
                }
                try
                {
                    room = objOnly.get(i).getJSONObject("room").getString("code");
                    map.put("room", room);
                }
                catch (Exception e){
                    map.put("room", "undefined");
                }
                if ((hour = getStartHour(objOnly.get(i).getString("start"))) != null)
                {
                    map.put("start", hour + "H");
                    //pour trier il me faut la date en long
                    try {
                        long way = formatGet.parse(objOnly.get(i).getString("start")).getTime();
                        String wayStr = String.valueOf(way);
                        map.put("date",wayStr);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                    _activities.add(map);
                }
            }
        }
       sortActivities();
    }
    //trie les activité en ordre chronologique

    private void sortActivities() {
        Collections.sort(_activities, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                if (lhs == null)
                {
                    if (rhs != null)
                        return -1;
                }
                int result = (int)Long.valueOf(lhs.get("date")).compareTo(Long.valueOf(rhs.get("date")));
                return result;
            }
        });
        String jourPrec = " ";
        String jour = null;
        //on retire les jours en trop écrit
        for (int i = 0; i < _activities.size();i++)
        {
            if ((jour = _activities.get(i).get("jour")) != null){
                if (jour.compareTo(jourPrec) == 0)
                {
                    _activities.get(i).remove("jour");
                }
                else
                    jourPrec = jour;
            }
        }
    }

    public String getActivityDayInFrench(String way)
    {
        String jour = null;
        Date dateActivity = null;

        try
        {
            dateActivity = formatGet.parse(way);
            jour = (String) this.date_equivalent.get(formatDay.format(dateActivity));
        }
        catch (Exception e)
        {
            return (null);
        }
        return (jour);
    }

    public void setequiv()
    {
        this.date_equivalent.put("Monday", "Lundi");

        this.date_equivalent.put("Tuesday", "Mardi");
        this.date_equivalent.put("Wednesday", "Mercredi");
        this.date_equivalent.put("Thursday", "Jeudi");
        this.date_equivalent.put("Friday", "Vendredi");
        this.date_equivalent.put("Saturday", "Samedi");
        this.date_equivalent.put("Sunday", "Dimanche");
    }

    public String getStartHour(String way)
    {
        String heure = null;
        Date dateActivity = null;
        try
        {
            dateActivity = formatGet.parse(way);
            heure = formatHour.format(dateActivity);
        }
        catch (Exception e)
        {
            return (null);
        }
        return (heure);
    }

    public List<HashMap<String, String>> get_activities() {
        return (_activities);
    }
}
