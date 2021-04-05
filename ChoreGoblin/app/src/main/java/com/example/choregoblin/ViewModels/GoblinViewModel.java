package com.example.choregoblin.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.choregoblin.DB.HouseDB;
import com.example.choregoblin.Data.Goblin;

import java.util.List;

public class GoblinViewModel extends AndroidViewModel {
    public final LiveData<List<Goblin>> allGoblins;
    public HouseDB db;

    public GoblinViewModel(Application application){
        super(application);
        createDb();
        allGoblins = db.goblinDao().getLiveGoblinList();
    }
    public void createDb() {
        db = HouseDB.getInstance(this.getApplication());
    }
}
