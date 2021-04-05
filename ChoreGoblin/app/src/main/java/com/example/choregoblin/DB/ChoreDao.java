package com.example.choregoblin.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.choregoblin.Data.Chore;

import java.util.List;

@Dao
public interface ChoreDao {
    @Query("Select * from Chore")
    List<Chore> getChoreList();
    @Query("DELETE FROM Chore")
    void nukeTable();
    @Insert
    void insertChore(Chore chore);
    @Update
    void updateChore(Chore chore);
    @Delete
    void deleteChore(Chore chore);
}
