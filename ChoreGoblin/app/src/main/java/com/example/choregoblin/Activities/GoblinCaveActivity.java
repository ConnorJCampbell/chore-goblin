package com.example.choregoblin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.choregoblin.Adapters.ChoreListAdapter;
import com.example.choregoblin.Adapters.GoblinListAdapter;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Interfaces.ServerCallback;
import com.example.choregoblin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GoblinCaveActivity extends BaseActivity {
    ListView listView;
    DataHandler dbHandler;
    GoblinListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initiateMenus();
        initDb();

        listView = findViewById(R.id.goblins_list);

        ArrayList<Goblin> activityGoblins = new ArrayList<Goblin>();
        adapter = new GoblinListAdapter(this, R.layout.goblin_item, activityGoblins);
        listView.setAdapter(adapter);

        updateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                //go to single goblin screen
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("Goblin", (Goblin)adapter.getItem(position));
//                Intent intent = new Intent(listView.getContext(), IndividualChoreActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
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

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean initiateMenus(){
        setContentView(R.layout.goblin_cave_activity);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        dbHandler = DataHandler.getInstance(this);
        dbHandler.loadHouse(this, new  ServerCallback(){
            @Override
            public void onSuccess(){
                nv.getMenu().getItem(5).setTitle("Goblin: "+ dbHandler.getHouse().getLoggedGoblin().getGoblinName());
                nv.getMenu().getItem(6).setTitle("Code: "+ dbHandler.getHouse().getHouseCode());
            }
        });
        return (true);
    }

    public boolean updateList(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList <Goblin> innerGoblins = new ArrayList<Goblin>(dbHandler.getDb().goblinDao().getGoblinList());
                adapter.setList(innerGoblins);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return(true);
    }

}