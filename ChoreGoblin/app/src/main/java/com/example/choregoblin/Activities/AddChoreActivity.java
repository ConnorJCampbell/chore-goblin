package com.example.choregoblin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import com.example.choregoblin.Data.House;
import com.example.choregoblin.Fragments.AddChoreTitleFragment;
import com.example.choregoblin.R;

public class AddChoreActivity extends BaseActivity implements AddChoreTitleFragment.OnClickListener{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateMenus();
        addName(getIntent().getExtras());
    }

    public void addName(Bundle bundle){
        FragmentManager fm = getSupportFragmentManager();

// add
        FragmentTransaction ft = fm.beginTransaction();
        AddChoreTitleFragment addChoreFrag = new AddChoreTitleFragment();
        addChoreFrag.setArguments(bundle);
        ft.add(R.id.fragment_container, addChoreFrag);

// alternatively add it with a tag
// trx.add(R.id.your_placehodler, new YourFragment(), "detail");
        ft.commit();

//// replace
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.your_placehodler, new YourFragment());
//        ft.commit();
//
//// remove
//        Fragment fragment = fm.findFragmentById(R.id.your_placehodler);
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.remove(fragment);
//        ft.commit();
    }
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof AddChoreTitleFragment) {
            AddChoreTitleFragment addChoreTitleFragment = (AddChoreTitleFragment) fragment;
            addChoreTitleFragment.setOnClickListener(this);
        }
    }

    public void onClick(int i){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    @Override
    public boolean initiateMenus(){
        try {
            setContentView(R.layout.add_chore_activity);
        } catch (Exception e){
            Log.e("Inf", "Erro inflating", e);
        }
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return (true);
    }


}
