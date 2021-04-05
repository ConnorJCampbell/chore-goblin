package com.example.choregoblin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Adapters.ChoreListAdapter;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.Enums.Chore_Status;
import com.example.choregoblin.Interfaces.ServerCallback;
import com.example.choregoblin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ChoreListActivity extends BaseActivity{
    ListView listView;
    DataHandler dbHandler;
    ChoreListAdapter adapter;
    House house;
    ArrayList<Goblin> goblins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateMenus();
        dbHandler = DataHandler.getInstance(this, house);
        listView = findViewById(R.id.chores_list);

        house = new House();
        goblins = new ArrayList<Goblin>();
//        house.setExternalId("1");
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<House> innerHouse = new ArrayList<House>(dbHandler.getDb().houseDao().getHouseList());
                final ArrayList<Goblin> innerGoblins = new ArrayList<Goblin>(dbHandler.getDb().goblinDao().getGoblinList());
                if (innerHouse.isEmpty()){
                    finish();
                }
//                Log.d("house check", innerHouse.get(0).getLoggedGoblin().getExternalId());
                house = innerHouse.get(0);
                dbHandler.setHouse(house);
                goblins = innerGoblins;
                nv.getMenu().getItem(5).setTitle("Goblin: "+ house.getLoggedGoblin().getGoblinName());
                nv.getMenu().getItem(6).setTitle("Code: "+ house.getHouseCode());
                updateList();
//                Log.d("house check", house.getExternalId());

            }
        });

        ArrayList <Chore> activityChores = new ArrayList<Chore>();
        adapter = new ChoreListAdapter(this, R.layout.chore_item, activityChores);
        listView.setAdapter(adapter);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                pullToRefresh.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v,int position, long id)
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Chore", (Chore)adapter.getItem(position));
                Intent intent = new Intent(listView.getContext(), IndividualChoreActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                updateList();
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //list is my listView

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                return(true);

            }
        });

        FloatingActionButton fab = findViewById(R.id.chores_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("House", house);
                Intent intent = new Intent(listView.getContext(), AddChoreActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }

    public void updateList(){
        dbHandler.getAllChoresFromDb(new ServerCallback() {
            @Override
            public void onSuccess() {
                final ArrayList <Chore> innerChores = dbHandler.getAllChores();
                adapter.setList(innerChores);
            }
        });
    }

    @Override
    public boolean initiateMenus(){
        try {
            setContentView(R.layout.chores_list_activity);
        } catch (Exception e){
            Log.e(TAG, "setting content view", e);
            throw e;
        }
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.chore_list_drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        return (true);
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


