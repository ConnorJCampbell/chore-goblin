package com.example.choregoblin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.Enums.Weekday;
import com.example.choregoblin.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HouseLoginActivity extends AppCompatActivity{
    House houseLogin;
    DataHandler dbHandler;
    Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_login_activity);

        final EditText houseCodeText = findViewById(R.id.house_login_editText);
        Button loginButton = (Button) findViewById(R.id.house_login_button);
        dbHandler = DataHandler.getInstance(this);
        thisContext = this;
        dbHandler.nukeHouse();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<House> innerHouse = new ArrayList<House>(dbHandler.getDb().houseDao().getHouseList());
                if (!innerHouse.isEmpty()){
                    Intent intent = new Intent(thisContext, ChoreListActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isEmpty(houseCodeText)){
                    Toast.makeText(v.getContext(), "Please enter a House Code", Toast.LENGTH_SHORT).show();
                }
                else{
                    String url = "http://131.104.48.205:3000/api/v1/houses/" + houseCodeText.getText().toString();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    JsonDeserializer<House> deserializer = new JsonDeserializer<House>() {
                                        @Override
                                        public House deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                            JsonObject jsonObject = json.getAsJsonObject();

                                            Weekday weekday;
                                            int weekdayVal =jsonObject.get("battle_interval").getAsInt();
                                            switch (weekdayVal){
                                                case (0):
                                                    weekday = Weekday.sunday;
                                                    break;
                                                case (1):
                                                    weekday = Weekday.monday;
                                                    break;
                                                case (2):
                                                    weekday = Weekday.tuesday;
                                                    break;
                                                case (3):
                                                    weekday = Weekday.wednesday;
                                                    break;
                                                case (4):
                                                    weekday = Weekday.thursday;
                                                    break;
                                                case (5):
                                                    weekday = Weekday.friday;
                                                    break;
                                                case (6):
                                                    weekday = Weekday.saturday;
                                                    break;
                                                default:
                                                    weekday = Weekday.sunday;
                                            }

                                            return new House(
                                                    jsonObject.get("id").getAsString(),
                                                    jsonObject.get("name").getAsString(),
                                                    jsonObject.get("code").getAsString(),
                                                    weekday
                                            );
                                        }
                                    };
                                    gsonBuilder.registerTypeAdapter(House.class, deserializer);
                                    Gson gson = gsonBuilder.create();
                                    houseLogin = gson.fromJson(response.toString(), House.class);
                                    dbHandler.nukeGoblins();
                                    dbHandler.nukeHouse();
                                    dbHandler.nukeChores();
                                    dbHandler.insertHouse(houseLogin);






                                    Intent intent = new Intent(thisContext, ChooseLoginGoblinActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("House", houseLogin);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("requestErr", "", error);
                                    Toast.makeText(thisContext, "Could not connect to the internet. Please try again later", Toast.LENGTH_SHORT).show();
                                }
                            });

                        // Access the RequestQueue through your singleton class.
                    OuterDbSingleton.getInstance(v.getContext()).addToRequestQueue(jsonObjectRequest);

                }
            }
        });
        Button newHouseButton = (Button) findViewById(R.id.house_login_new_house_button);
        newHouseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    Intent intent = new Intent(thisContext, NewHouseActivity.class);
                    startActivity(intent);

            }
        });

    }
    public void login(View view){

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
