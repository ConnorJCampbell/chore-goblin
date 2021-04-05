package com.example.choregoblin.Data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.HouseDB;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Enums.Chore_Status;
import com.example.choregoblin.Interfaces.ServerCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataHandler {
    private static DataHandler instance;
    private ArrayList <Goblin> goblins;
    private ArrayList <Chore> chores;
    private static Context ctx;
    private House house;
    protected HouseDB db;

    private DataHandler(Context context){
        ctx = context;
        db = getDb();
        house = new House();
        goblins = new ArrayList<Goblin>();
        chores = new ArrayList<Chore>();
    }
    private DataHandler(Context context, House house){
        ctx = context;
        db = getDb();
        this.house = house;
    }

    public static synchronized DataHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DataHandler(context);
            
        }
        return instance;
    }
    public static synchronized DataHandler getInstance(Context context, House house) {
        if (instance == null) {
            instance = new DataHandler(context, house);
        }
        return instance;
    }

    private boolean initiateDb(){
        db = HouseDB.getInstance(ctx);
        return(true);
    }

    public HouseDB getDb() {
        if (db == null){
            initiateDb();
        }
        return db;
    }

    public ArrayList<Chore> getAllChores(){
        final ArrayList <Chore> choreList = new ArrayList<Chore>(db.choreDao().getChoreList());
        chores = new ArrayList<>(choreList);
        return chores;
    }

    public void getAllChoresFromDb(final ServerCallback callback){
        String url = "http://131.104.48.205:3000/api/v1/goblinList/" + house.getExternalId();
        JsonArrayRequest goblinsJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                nukeGoblins();
                goblins.clear();
                for (int i = 0;i<response.length(); i++){
                    Goblin newGoblin = new Goblin();
                    JSONObject jsonobject = new JSONObject();
                    try {
                        jsonobject = (JSONObject) response.get(i);
                    } catch (Exception e){
                        Log.d("ERR", "no idea what to do if this fails tbh");
                    }

                    try {
                        int [] stats= new int[5];
                        newGoblin.setExternalId(jsonobject.optString("id"));
                        newGoblin.setGoblinOwner(jsonobject.optString("owner_name"));
                        newGoblin.setGoblinName(jsonobject.optString("goblin_name"));
                        stats[0] = jsonobject.optInt("strength");
                        stats[1] = jsonobject.optInt("speed");
                        stats[2] = jsonobject.optInt("defense");
                        stats[3] = jsonobject.optInt("hp");
                        stats[4] = jsonobject.optInt("free_points");
                        newGoblin.setStats(stats);
                        newGoblin.setHouseId(jsonobject.optString("house_id"));
                        insertGoblin(newGoblin);
                        goblins.add(newGoblin);
//                        Log.d("dbR", "Goblin: " + newGoblin.toString());
                    } catch (Exception e){
                        Log.d("ERR", "adding goblin failed");

                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,"Could not reach server", Toast.LENGTH_SHORT).show();
                chores = new ArrayList<Chore>(getDb().choreDao().getChoreList());
            }
        });
        String choresUrl = "http://131.104.48.205:3000/api/v1/choreList/" + house.getExternalId();
        JsonArrayRequest choresJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, choresUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                nukeChores();
//                Log.d("Testing", "pre loop " + response.length());
                for (int i = 0;i<response.length(); i++){
                    Chore newChore = new Chore();
                    JSONObject jsonobject = new JSONObject();

//                    Log.d("Testing", "pre try catch");
                    try {
                        jsonobject = (JSONObject) response.get(i);
                    } catch (Exception e){
//                        Log.d("ERR", "no idea what to do if this fails tbh");
                    }
//                    Log.d("Testing", "pre object reading");
                    newChore.setExternalId(jsonobject.optString("id"));
                    newChore.setTitle(jsonobject.optString("title"));
                    newChore.setStatus(Chore_Status.valueOf(jsonobject.optString("status")));
                    newChore.setEffortValue(jsonobject.optInt("effort_value"));
                    newChore.setHouseId(jsonobject.optString("house_id"));
                    newChore.setGoblinAssignee(searchGoblinsByExternalId(goblins, jsonobject.optInt("goblin_id")));
                    insertChore(newChore);
//                    Log.d("gid", "looking for " + jsonobject.optInt("goblin_id") + "possible" + goblins.toString());
                }
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        chores = new ArrayList<Chore>(getDb().choreDao().getChoreList());
                        callback.onSuccess();
                    }
                });

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,"Could not reach server", Toast.LENGTH_SHORT).show();
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        chores = new ArrayList<Chore>(getDb().choreDao().getChoreList());
                    }
                });
            }
        });

        // Access the RequestQueue through your singleton class.
        OuterDbSingleton.getInstance(ctx).addToRequestQueue(goblinsJsonArrayRequest);
        OuterDbSingleton.getInstance(ctx).addToRequestQueue(choresJsonArrayRequest);
    }

    public void insertChore(final Chore newChore){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.choreDao().insertChore(newChore);
            }
        });
    }

    public void deleteChore(final Chore newChore){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.choreDao().deleteChore(newChore);
            }
        });
    }

    public void insertHouse(final House house){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.houseDao().insertHouse(house);
            }
        });
    }
    public void insertGoblin(final Goblin newGoblin){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.goblinDao().insertGoblin(newGoblin);
            }
        });
    }
    public Goblin searchGoblinsByExternalId(ArrayList<Goblin> goblins, int id){
        for (int i = 0; i < goblins.size(); i++){
            Log.d("GBS", "[" + goblins.get(i).getExternalId() + "]" + "[" + id + "]" );
            if (goblins.get(i).getExternalId().compareTo(Integer.toString(id)) == 0)
                return(goblins.get(i));
        }
        Goblin goblin = new Goblin();
        return(goblin);
    }



    public void nukeGoblins(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.goblinDao().nukeGoblins();
            }
        });
    }
    public void nukeChores(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.choreDao().nukeTable();
            }
        });
    }
    public void nukeHouse(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.houseDao().nukeTable();
            }
        });
    }

    public ArrayList<Goblin> getGoblins() {
        return goblins;
    }

    public void setGoblins(ArrayList<Goblin> goblins) {
        this.goblins = goblins;
    }

    public ArrayList<Chore> getChores() {
        return chores;
    }

    public void setChores(ArrayList<Chore> chores) {
        this.chores = chores;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void loadHouse(Context context, final ServerCallback callback) {
        if (house == null){
            ctx = context;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    house = db.houseDao().getHouseList().get(0);
                    callback.onSuccess();
                }
            });
        } else {
            callback.onSuccess();
        }
    }

    public House getHouse(){
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public void setDb(HouseDB db) {
        this.db = db;
    }
}
