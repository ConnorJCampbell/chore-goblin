package com.example.choregoblin.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.choregoblin.Data.Goblin;

import java.util.List;

@Dao
public interface GoblinDao {
    @Query("Select * from goblin")
    List<Goblin> getGoblinList();
    @Query("Select * from goblin")
    LiveData<List<Goblin>> getLiveGoblinList();
    @Query("SELECT name FROM goblin")
    List<String> getAllGoblinNames();
    @Query("DELETE FROM goblin")
    void nukeGoblins();
    @Insert
    void insertGoblin(Goblin goblin);
    @Update
    void updateGoblin(Goblin goblin);
    @Delete
    void deleteGoblin(Goblin goblin);

}
