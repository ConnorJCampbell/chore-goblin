package com.example.choregoblin.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.choregoblin.Data.House;

import java.util.List;

@Dao
public interface HouseDao{
    @Query("Select * from house")
    List<House> getHouseList();
    @Query("DELETE FROM house")
    void nukeTable();
    @Insert
    void insertHouse(House house);
    @Update
    void updateHouse(House house);
    @Delete
    void deleteHouse(House house);
}
