package com.example.choregoblin.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.example.choregoblin.Enums.Goblin_Stats;

import java.io.Serializable;

@Entity
public class Goblin implements Serializable {
    //goblin image
    @PrimaryKey (autoGenerate = true)
    private int id;
    @ColumnInfo(name = "stats")
    private int [] stats  = new int[5];
    @ColumnInfo(name = "name")
    private String goblinName;
    @ColumnInfo(name = "owner")
    private String goblinOwner;
    @ColumnInfo(name = "externalId")
    private String externalId;
    @ColumnInfo(name = "houseId")
    private String houseId;

    public Goblin(/*TO DO: PICTURE*/int[] stats, String goblinName, String goblinOwner){
        if (stats.length!= 5)
            stats = new int[5]; //idk if this is legal tbh
        this.stats = stats;
        this.goblinName = goblinName;
        this.goblinOwner = goblinOwner;
    }
    @Ignore
    public Goblin(){
        stats = new int[5]; //idk if this is legal tbh
        this.goblinName = "LOADINGVALUE";
        this.goblinOwner = "LOADINGVALUE";
        this.externalId = "-1";
    }

    //to do
    //get/set Image


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getStats() {
        return stats;
    }

    public void setStats(int[] stats) {
        this.stats = stats;
    }

    public int getStrength(){
        return(stats[Goblin_Stats.strength.ordinal()]);
    }
    public int getSpeed(){
        return(stats[Goblin_Stats.speed.ordinal()]);
    }
    public int getDefense(){
        return(stats[Goblin_Stats.defense.ordinal()]);
    }
    public int getHealth(){
        return(stats[Goblin_Stats.healthPoints.ordinal()]);
    }
    public int getFreePoints(){
        return(stats[Goblin_Stats.freePoints.ordinal()]);
    }
    public String getGoblinName() {
        return goblinName;
    }
    public String getGoblinOwner() {
        return goblinOwner;
    }
    public String getExternalId() {
        return externalId;
    }
    public String getHouseId() {
        return houseId;
    }



    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    public void setStrength(int newStrength){
        this.stats[Goblin_Stats.strength.ordinal()] = newStrength;
    }
    public void setSpeed(int newSpeed){
        this.stats[Goblin_Stats.speed.ordinal()] = newSpeed;
    }
    public void setDefense(int newDefense){
        this.stats[Goblin_Stats.defense.ordinal()] = newDefense;
    }
    public void setHealthPoints(int newHealthPoints){
        this.stats[Goblin_Stats.healthPoints.ordinal()] = newHealthPoints;
    }
    public void setFreePoints(int newFreePoints){
        this.stats[Goblin_Stats.freePoints.ordinal()] = newFreePoints;
    }
    public void setGoblinName(String goblinName) {
        this.goblinName = goblinName;
    }
    public void setGoblinOwner(String goblinOwner) {
        this.goblinOwner = goblinOwner;
    }

    @Override
    public String toString(){
        return ("[" + goblinName + "], [" + goblinOwner + "], [" + externalId + "], [" + houseId + "]");
    }
}
