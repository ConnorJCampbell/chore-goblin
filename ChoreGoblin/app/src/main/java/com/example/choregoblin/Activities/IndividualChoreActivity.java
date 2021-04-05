package com.example.choregoblin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.Enums.Chore_Status;
import com.example.choregoblin.R;

import org.json.JSONObject;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class IndividualChoreActivity extends BaseActivity{
    Context ctx;
    TextView titleTextView;
    TextView effortValueTextView;
    TextView statusTextView;
    TextView goblinAssigneeTextView;

    protected void onCreate(Bundle savedInstanceState) {
        ctx = this;
        super.onCreate(savedInstanceState);
        initiateMenus();
        populateActivity((Chore)getIntent().getExtras().getSerializable("Chore"));
    }

    public void populateActivity(Chore chore){
        titleTextView = findViewById(R.id.individual_chore_title);
        effortValueTextView = findViewById(R.id.individual_chore_effort_value);
        statusTextView = findViewById(R.id.individual_chore_status);
        goblinAssigneeTextView = findViewById(R.id.individual_chore_goblin_assignee);

        String titleFormatting = chore.getTitle();
        titleTextView.setText(titleFormatting);



        String statusFormatting = "Status: " + chore.getStatus().toString();
        statusTextView.setText(statusFormatting);


        String effortValueFormatting = "Effort value:" + chore.getEffortValue();
        effortValueTextView.setText(effortValueFormatting);

        String goblinAssignee = "Assigned to:" + chore.getGoblinAssignee().getGoblinName();
        goblinAssigneeTextView.setText(goblinAssignee);


        initiateButtons(chore);

    }

    public void initiateButtons(Chore chore){
        final Chore reference = chore;
        Button editChoreButton = findViewById(R.id.edit_chore_button);
        Button completeChoreButton = findViewById(R.id.complete_chore_button);
        Button deleteChoreButton = findViewById(R.id.delete_chore_button);

        editChoreButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Chore", reference);
                        Intent intent = new Intent(ctx, AddChoreActivity.class);
                        final ArrayList<House> innerHouse = new ArrayList<House>(dbHandler.getDb().houseDao().getHouseList());
                        bundle.putSerializable("House",innerHouse.get(0));
                        intent.putExtras(bundle);
                        startActivityForResult(intent,1);
                    }
                });

            }
        });
        completeChoreButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final String choreDelUrl = "http://131.104.48.205:3000/api/v1/chores/" + reference.getExternalId();
                        JSONObject choreJson = new JSONObject();
                        try {
                            choreJson = new JSONObject().put("id", reference.getExternalId())
                                    .put("title", reference.getTitle())
                                    .put("status", Chore_Status.complete)
                                    .put("effort_value", reference.getEffortValue())
                                    .put("house_id", reference.getHouseId())
                                    .put("goblin_id", reference.getGoblinAssignee().getExternalId());
                        } catch (Exception e) {
                            Log.e("loser", "Wow you suck at programming don't you", e);
                        }
                        JsonObjectRequest completeChore = new JsonObjectRequest(Request.Method.PUT, choreDelUrl, choreJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                reference.setStatus(Chore_Status.complete);
                                String statusFormatting = "Status: " + reference.getStatus().toString();
                                Log.d("Status", statusFormatting);
                                statusTextView.setText(statusFormatting);
                            }
                        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Status", "update failed");
                            }
                        });
                        OuterDbSingleton.getInstance(ctx).addToRequestQueue(completeChore);
                    }
                });

            }
        });
        deleteChoreButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final String choreDelUrl = "http://131.104.48.205:3000/api/v1/chores/" + reference.getExternalId();
                        JsonObjectRequest deleteChore = new JsonObjectRequest(Request.Method.DELETE, choreDelUrl, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        OuterDbSingleton.getInstance(ctx).addToRequestQueue(deleteChore);
                    }
                });

            }
        });
    }

    @Override
    public boolean initiateMenus(){
        try {
            setContentView(R.layout.individual_chore_activity);
        } catch (Exception e){
            Log.e(TAG, "setting content view", e);
            throw e;
        }
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return (true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
//                Log.d("RESULT", "it worked");
                setResult(RESULT_OK);
                finish();
            }
        }
    }//onActivityResult


}
