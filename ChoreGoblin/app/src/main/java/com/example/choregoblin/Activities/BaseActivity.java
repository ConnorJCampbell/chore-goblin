package com.example.choregoblin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.choregoblin.DB.AppExecutors;
import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.DB.HouseDB;
import com.example.choregoblin.Data.DataHandler;
import com.example.choregoblin.R;
import com.google.android.material.navigation.NavigationView;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout dl;
    protected NavigationView nv;
    protected Toolbar tb;
    protected DataHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateMenus();
        initDb();
    }


    protected void initDb(){
        dbHandler = DataHandler.getInstance(this);
    }

    public boolean initiateMenus(){
        setContentView(R.layout.base_activity);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);
        nv.setNavigationItemSelectedListener(this);
        tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        return (true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        switch(id) {
            case R.id.toolbar_menu:
                dl.openDrawer(GravityCompat.START);
                break;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem ) {

        int id = menuItem.getItemId();
        Intent intent;
        switch(id)
        {

            case R.id.nav_chores:
                intent = new Intent(this, ChoreListActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.nav_my_goblin:
                Toast.makeText(this, "GO TO MY GOBLIN",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_history:
                Toast.makeText(this, "GO TO HISTORY",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cave:
                intent = new Intent(this, GoblinCaveActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                return true;
        }
        dl.closeDrawer(GravityCompat.START);
        nv.setCheckedItem(id);
        return (true);
    }
}