package com.example.choregoblin.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.choregoblin.Enums.Chore_Status;

import java.io.Serializable;

@Entity(tableName = "Chore")
public class Chore implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "status")
    private Chore_Status status;
    @ColumnInfo(name = "effortValue")
    private int effortValue;
    @ColumnInfo(name = "goblinAssignee")
    private Goblin goblinAssignee;
    @ColumnInfo(name = "externalId")
    private String externalId;
    @ColumnInfo(name = "houseId")
    private String houseId;

    public Chore(String title, Chore_Status status, int effortValue, Goblin goblinAssignee){
        super();
        this.title = title;
        this.status = status;
        this.effortValue = effortValue;
        this.goblinAssignee = goblinAssignee;
    }
    @Ignore
    public Chore (){
        super();
        this.title = "EMPTY_TITLE";
        this.status = Chore_Status.incomplete;
        this.effortValue = 4;
        this.goblinAssignee = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle(){
        return(title);
    }

    public Chore_Status getStatus(){
        return(status);
    }

    public int getEffortValue(){
        return(effortValue);
    }

    public Goblin getGoblinAssignee(){
        return(goblinAssignee);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setTitle(String newTitle){
        title = newTitle;
    }

    public void setStatus(Chore_Status newStatus){
        status = newStatus;
    }

    public void setEffortValue(int newEffortValue){
        effortValue = newEffortValue;
    }

    public void setGoblinAssignee(Goblin newGoblinAssignee){
        goblinAssignee = newGoblinAssignee;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String toString(){
        return("[" + title + "], [" + status + "], [" + effortValue + "], [" + getGoblinAssignee().getGoblinName() + "]");
    }
}
