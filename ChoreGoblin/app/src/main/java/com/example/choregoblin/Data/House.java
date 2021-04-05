package com.example.choregoblin.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.choregoblin.Enums.Weekday;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

@Entity(tableName = "house")
public class House implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "externalId")
    private String externalId;
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "houseCode")
    private String houseCode;
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "name")
    private String name;
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "interval")
    private Weekday interval;
    @Expose
    @ColumnInfo(name = "loggedGoblin")
    private Goblin loggedGoblin;

    public House(String externalId,String name, String houseCode, Weekday interval){
        this.externalId = externalId;
        this.name = name;
        this.houseCode = houseCode;
        this.interval = interval;
    }
    @Ignore
    public House(){
        this.externalId = "-1";
        this.name = "Jeremy";
        this.houseCode = "12345";
        this.interval = Weekday.friday;
        this.loggedGoblin = new Goblin();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public String getExternalId() {
        return externalId;
    }

    public Weekday getInterval() {
        return interval;
    }

    public Goblin getLoggedGoblin() {
        return loggedGoblin;
    }

    public void setLoggedGoblin(Goblin loggedGoblin) {
        this.loggedGoblin = loggedGoblin;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public void setInterval(Weekday interval) {
        this.interval = interval;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){
        return("House: [" + externalId + "][" + houseCode + "][" + name + "][" + interval.toString() + "][" + loggedGoblin.toString() + "]\n");
    }

}
