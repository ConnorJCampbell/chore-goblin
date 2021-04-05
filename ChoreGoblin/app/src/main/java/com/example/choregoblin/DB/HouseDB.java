package com.example.choregoblin.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Data.House;

@Database(entities = {House.class, Goblin.class, Chore.class}, exportSchema = false, version = 1)
@TypeConverters({DataConverters.class})
public abstract class HouseDB extends RoomDatabase {
    private static final String DB_NAME = "house_db";
    private static HouseDB instance;

    public static synchronized HouseDB getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), HouseDB.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract HouseDao houseDao();
    public abstract GoblinDao goblinDao();
    public abstract ChoreDao choreDao();
}
