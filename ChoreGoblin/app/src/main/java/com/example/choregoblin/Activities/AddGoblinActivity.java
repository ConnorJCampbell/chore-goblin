package com.example.choregoblin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.R;

import org.json.JSONObject;

public class AddGoblinActivity extends AppCompatActivity{
    House house;
    Toolbar tb;
    Goblin newGoblin;
    DataHandler dbHandler;
    Context ctx;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goblin_activity);
        tb = findViewById(R.id.login_goblin_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHandler = DataHandler.getInstance(this);
        newGoblin = new Goblin();
        ctx = this;
        final EditText newGoblinName = findViewById(R.id.goblin_name_edit);
        final EditText newGoblinOwner = findViewById(R.id.goblin_owner_edit);
        house = (House)getIntent().getExtras().getSerializable("House");



        Button summonGoblinButton = findViewById(R.id.summon_goblin);
        summonGoblinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(newGoblinName)){
                    Toast.makeText(v.getContext(),"Please enter a Goblin Name", Toast.LENGTH_SHORT).show();
                }
                else if(isEmpty(newGoblinOwner)){
                    Toast.makeText(v.getContext(),"Please enter an Owner Name", Toast.LENGTH_SHORT).show();
                } else {
                    final String url = "http://131.104.48.205:3000/api/v1/goblins";
                    JSONObject goblinJson = new JSONObject();
                    try {
                        goblinJson = new JSONObject().put("goblin_name", newGoblinName.getText())
                                .put("owner_name", newGoblinOwner.getText())
                                .put("house_id", house.getExternalId());
                    } catch (Exception e) {
                        Log.e("loser", "Wow you suck at programming don't you", e);
                    }
                    JsonObjectRequest postGoblin = new JsonObjectRequest(Request.Method.POST, url, goblinJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            newGoblin.setExternalId(response.optString("id"));
                            newGoblin.setGoblinOwner(response.optString("owner_name"));
                            int [] stats = {response.optInt("strength")
                                    , response.optInt("speed")
                                    , response.optInt("defense")
                                    , response.optInt("hp")
                                    , response.optInt("free_points")};
                            newGoblin.setStats(stats);
                            newGoblin.setHouseId(response.optString("house_id"));
                            house.setLoggedGoblin(newGoblin);
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    dbHandler.getDb().houseDao().updateHouse(house);
                                }
                            });
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    OuterDbSingleton.getInstance(v.getContext()).addToRequestQueue(postGoblin);
                }
            }
        });

    }




    public void onClick(View v) {

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}


