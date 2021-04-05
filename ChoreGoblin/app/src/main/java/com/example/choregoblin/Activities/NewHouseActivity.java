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
import com.example.choregoblin.Enums.Weekday;
import com.example.choregoblin.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class NewHouseActivity extends AppCompatActivity {
    House house;
    Toolbar tb;
    Context ctx;
    DataHandler dbHandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_house_activity);
        house = new House();
        ctx = this;
        tb = findViewById(R.id.new_house_toolbar);
        dbHandler = DataHandler.getInstance(this);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditText newHouseName = findViewById(R.id.new_house_name_edit);

        Button createLairButton = findViewById(R.id.create_lair_button);
        createLairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(newHouseName)){
                    Toast.makeText(v.getContext(),"Please enter a House Name", Toast.LENGTH_SHORT).show();
                } else {
                    final String url = "http://131.104.48.205:3000/api/v1/houses";
                    JSONObject choreJson = new JSONObject();
                    try {
                        choreJson = new JSONObject().put("name", newHouseName.getText())
                                .put("battle_interval", Weekday.sunday.ordinal());
                    } catch (Exception e) {
                        Log.e("loser", "Wow you suck at programming don't you", e);
                    }
                    JsonObjectRequest postGoblin = new JsonObjectRequest(Request.Method.POST, url, choreJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            house.setExternalId(response.optString("id"));
                            house.setHouseCode(response.optString("code"));
                            house.setName(response.optString("name"));
                            house.setInterval(Weekday.sunday);
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    dbHandler.getDb().houseDao().insertHouse(house);
                                }
                            });
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("House", house);
                            Intent intent = new Intent(ctx, AddGoblinActivity.class);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ctx, "Could not connect to the internet. Please try again later", Toast.LENGTH_SHORT).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Bundle bundle = new Bundle();
                bundle.putSerializable("House", house);
                Intent intent = new Intent(ctx, ChooseLoginGoblinActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }
    }//onActivityResult
}



