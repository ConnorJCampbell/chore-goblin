package com.example.choregoblin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.choregoblin.Adapters.ChoreListAdapter;
import com.example.choregoblin.Adapters.GoblinListAdapter;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseLoginGoblinActivity extends BaseActivity {
    ListView listView;
    DataHandler dbHandler;
    GoblinListAdapter adapter;
    House house;
    ArrayList<Goblin> goblins;
    public static Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initiateMenus();
        initDb();

        dbHandler = DataHandler.getInstance(this);
        listView = findViewById(R.id.login_goblins_list);
        house = (House)getIntent().getExtras().getSerializable("House");

        ArrayList<Goblin> activityGoblins = new ArrayList<Goblin>();
        goblins  = new ArrayList<Goblin>();
        adapter = new GoblinListAdapter(this, R.layout.goblin_item, activityGoblins);
        listView.setAdapter(adapter);

        updateList();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.goblins_pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                pullToRefresh.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                Log.d("SEL", goblins.get(position).getExternalId());
                house.setLoggedGoblin(goblins.get(position));
                Log.d("SEL", house.getLoggedGoblin().getExternalId());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(listView.getContext(), ChoreListActivity.class);
                        dbHandler.getDb().houseDao().nukeTable();
                        dbHandler.getDb().houseDao().insertHouse(house);
                        startActivity(intent);
                        finish();
                    }
                });

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //list is my listView

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
//                final Chore toBeDeleted = (Chore)adapter.getItem(pos);
//                dbHandler.deleteChore(toBeDeleted);
//                updateList();
//                Toast.makeText(getBaseContext(), "YEET", Toast.LENGTH_LONG).show();
                return(true);

            }
        });

        FloatingActionButton fab = findViewById(R.id.goblins_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("House", house);
                Intent intent = new Intent(listView.getContext(), AddGoblinActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean initiateMenus(){
        setContentView(R.layout.choose_login_goblin);
        tb = findViewById(R.id.login_goblin_my_toolbar);
        setSupportActionBar(tb);
        return (true);
    }

    public boolean updateList(){
        String url = "http://131.104.48.205:3000/api/v1/goblinList/" + house.getExternalId();
//        Log.d("URL:", "{" + url + "]");
        JsonArrayRequest goblinsJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dbHandler.nukeGoblins();
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
                        dbHandler.insertGoblin(newGoblin);
                        goblins.add(newGoblin);
//                        Log.d("dbR", "Goblin: " + newGoblin.toString());
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(goblins);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    } catch (Exception e){
                        Log.d("ERR", "adding goblin failed");

                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(listView.getContext(),"Could not reach server", Toast.LENGTH_SHORT).show();
            }
        });
        OuterDbSingleton.getInstance(listView.getContext()).addToRequestQueue(goblinsJsonArrayRequest);

        return(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        return false;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
//                Log.d("RESULT", "it worked");
                updateList();
            }
        }
    }//onActivityResult
}