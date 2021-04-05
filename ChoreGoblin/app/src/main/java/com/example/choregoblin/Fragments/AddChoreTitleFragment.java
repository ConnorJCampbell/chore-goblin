package com.example.choregoblin.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.DB.OuterDbSingleton;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;
import com.example.choregoblin.Enums.Chore_Status;
import com.example.choregoblin.R;
import com.example.choregoblin.ViewModels.GoblinViewModel;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//This is a fragment because eventually it will be multiple fragments. For the alpha it is only one fragment
public class AddChoreTitleFragment extends Fragment implements View.OnClickListener{

    private OnClickListener callback;
    private House house;
    private Chore potentialChoreEdit;
    private EditText choreTitle;
    private EditText effortValue;
    private Spinner goblinSpinner;
    private DataHandler dbHandler;
    private GoblinViewModel viewModel;
    private List<Goblin> allGoblins;
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_chore_title_fragment,container, false);
        ctx = this.getContext();
        setTitleText(view, this.getString(R.string.add_chore_title_title));
        dbHandler = DataHandler.getInstance(view.getContext());


        effortValue = view.findViewById(R.id.add_chore_effort_value_text);
        choreTitle = view.findViewById(R.id.add_chores_title_text);
        goblinSpinner =(Spinner) view.findViewById(R.id.add_chore_select_goblin);
        viewModel = ViewModelProviders.of(this).get(GoblinViewModel.class);


        Button nextButton = (Button) view.findViewById(R.id.add_chore_title_button);
        nextButton.setOnClickListener(this);
        Bundle bundle = getArguments();
        house = (House)bundle.getSerializable("House");
        try{
            potentialChoreEdit = (Chore)bundle.getSerializable("Chore");
            setDefaultTitleEditText(view,potentialChoreEdit.getTitle());
            setDefaultEffortEditText(view, "" + potentialChoreEdit.getEffortValue());
        } catch (Exception e){

        }


        final View spinnerView = view;

        final ArrayList <String> goblins = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinnerView.getContext(), android.R.layout.simple_spinner_dropdown_item, goblins);

        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        goblinSpinner.setAdapter(adapter);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList <String> goblins = new ArrayList<String>(dbHandler.getDb().goblinDao().getAllGoblinNames());
                Log.d("goblins", "gb: "+ goblins.toString());
                adapter.addAll(goblins);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        adapter.notifyDataSetChanged();
        subscribeGoblins();

        return view;
    }

    public void onClick(View v) {
        //do what you want to do when button is clicked
        if (isEmpty(choreTitle)){
            Toast.makeText(v.getContext(), "Please enter a Chore Title",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(isEmpty(effortValue)){
            Toast.makeText(v.getContext(), "Please enter an effort value",Toast.LENGTH_SHORT).show();
            return;
        }
//        else if(effortValue.getText() == null){
//            Toast.makeText(v.getContext(), "Please enter an effort value",Toast.LENGTH_SHORT).show();
//        }




        Log.d("Add Chore",choreTitle.getText().toString() + "\n" + Chore_Status.incomplete.toString() + "\n" + Integer.parseInt(effortValue.getText().toString()) + "\n" + allGoblins.get(goblinSpinner.getSelectedItemPosition()));
        Chore newChore = new Chore(choreTitle.getText().toString(), Chore_Status.incomplete, Integer.parseInt(effortValue.getText().toString()), allGoblins.get(goblinSpinner.getSelectedItemPosition()));
        dbHandler.insertChore(newChore);

        //send to db
        final String url = "http://131.104.48.205:3000/api/v1/chores";

        JSONObject choreJson = new JSONObject();
        try {
             choreJson = new JSONObject().put("title", newChore.getTitle())
                    .put("house_id", house.getExternalId())
                    .put("goblin_id", allGoblins.get(goblinSpinner.getSelectedItemPosition()).getExternalId())
                    .put("effort_value", effortValue.getText());
        } catch (Exception e) {
            Log.e("loser", "Wow you suck at programming don't you", e);
        }
        JsonObjectRequest putChore = new JsonObjectRequest(Request.Method.POST, url, choreJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (potentialChoreEdit != null){
                    final String choreDelUrl = "http://131.104.48.205:3000/api/v1/chores/" + potentialChoreEdit.getExternalId();
                    JsonObjectRequest deleteChore = new JsonObjectRequest(Request.Method.DELETE, choreDelUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    OuterDbSingleton.getInstance(ctx).addToRequestQueue(deleteChore);
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        OuterDbSingleton.getInstance(v.getContext()).addToRequestQueue(putChore);
        callback.onClick(1);
    }


    public void setTitleText(View parentView, String text) {
        TextView view = (TextView) parentView.findViewById(R.id.add_chores_title_title);
        view.setText(text);
    }

    public void setDefaultTitleEditText(View parentView, String text) {
        EditText view = (EditText) parentView.findViewById(R.id.add_chores_title_text);
        view.setText(text);
    }

    public void setDefaultEffortEditText(View parentView, String text) {
        EditText view = (EditText) parentView.findViewById(R.id.add_chore_effort_value_text);
        view.setText(text);
    }

    public void setOnClickListener(OnClickListener callback) {
        this.callback = callback;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface OnClickListener {
        public void onClick(int position);
    }

    public void updateGoblinList(List<Goblin> goblins){
        allGoblins = goblins;
    }

    private void subscribeGoblins() {
        viewModel.allGoblins.observe(this, new Observer<List<Goblin>>() {
            @Override
            public void onChanged(@NonNull final List<Goblin> goblins) {
                updateGoblinList(goblins);
            }
        });
    }
}
