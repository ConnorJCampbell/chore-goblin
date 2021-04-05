package com.example.choregoblin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.Enums.Weekday;
import com.example.choregoblin.Fragments.AddChoreTitleFragment;
import com.example.choregoblin.R;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {

    House house;
    Context ctx;
    DataHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        house = new House();
        ctx = this;
        initiateMenus();
        dbHandler = DataHandler.getInstance(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<House> innerHouse = new ArrayList<House>(dbHandler.getDb().houseDao().getHouseList());
                if (innerHouse.isEmpty()){
                    finish();
                }
                house = innerHouse.get(0);
                nv.getMenu().getItem(5).setTitle("Goblin: "+ house.getLoggedGoblin().getGoblinName());
                nv.getMenu().getItem(6).setTitle("Code: "+ house.getHouseCode());
                Log.d("del", house.getName());
                initiateButtons();
                populateHouseCodeText();

            }
        });
    }

    public void populateHouseCodeText(){
        TextView houseCode = findViewById(R.id.house_code);
        houseCode.setText(house.getHouseCode());
    }

    @Override
    public boolean initiateMenus(){
        setContentView(R.layout.settings_activity);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        return (true);
    }

    public void initiateButtons(){
        Button chooseGoblinButton = findViewById(R.id.choose_goblin_button);
        Button deleteGoblinButton = findViewById(R.id.delete_goblin_button);
        Button deleteHouseButton = findViewById(R.id.delete_house_button);
        Button logoutButton = findViewById(R.id.logout_button);

        chooseGoblinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), ChooseLoginGoblinActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("House", house);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        deleteGoblinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = "http://131.104.48.205:3000/api/v1/goblins/" + house.getLoggedGoblin().getExternalId();
                Log.d("url", url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(ctx, ChooseLoginGoblinActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("House", house);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("requestErr", "", error);
                        Toast.makeText(ctx, "Could not connect to the internet. Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });

                // Access the RequestQueue through your singleton class.
                OuterDbSingleton.getInstance(v.getContext()).addToRequestQueue(jsonObjectRequest);

            }
        });
        deleteHouseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String url = "http://131.104.48.205:3000/api/v1/houses/" + house.getHouseCode();
                Log.d("url", url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(ctx, HouseLoginActivity.class);
                        dbHandler.nukeChores();
                        dbHandler.nukeGoblins();
                        dbHandler.nukeHouse();
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("requestErr", "", error);
                        Toast.makeText(ctx, "Could not connect to the internet. Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });

                // Access the RequestQueue through your singleton class.
                OuterDbSingleton.getInstance(v.getContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ctx, HouseLoginActivity.class);
                dbHandler.nukeChores();
                dbHandler.nukeGoblins();
                dbHandler.nukeHouse();
                startActivity(intent);
                finish();
                // Access the RequestQueue through your singleton class.
            }
        });
    }

}