package com.example.choregoblin.DB;

import android.util.Log;

import androidx.room.TypeConverter;

import com.example.choregoblin.Enums.Chore_Status;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.Enums.Weekday;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DataConverters {
    @TypeConverter
    public static ArrayList<Goblin> goblinListFromString(String value){
        Type listType = new TypeToken<ArrayList<Goblin>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayList(ArrayList<Goblin> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Goblin goblinFromString(String value){
        Gson gson = new Gson();
        return(gson.fromJson(value, Goblin.class));
    }
    @TypeConverter
    public static String stringFromGoblin(Goblin goblin){
        Gson gson = new Gson();
        return(gson.toJson(goblin));
    }




    @TypeConverter
    public static Weekday fromInt(int value){
        return Weekday.values()[value];
    }
    @TypeConverter
    public static int fromWeekday(Weekday value){
        return(value.ordinal());
    }
    @TypeConverter
    public static int[] fromStringToInt(String value){
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(value);
        } catch (Exception e){
            Log.e(TAG, "beans", e);
        }
        JSONArray array = obj.optJSONArray("stats");
        int[] stats = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            stats[i] = array.optInt(i);
        return stats;
    }
    @TypeConverter
    public static String fromInt(int[] value){
        JSONArray newArray = new JSONArray();
        for(int i = 0; i < value.length; i++){
            newArray.put(value[i]);
        }
        JSONObject result = new JSONObject();
        try {
            result.put("stats", newArray);
        } catch (Exception e){
            Log.e(TAG, "mash", e);
        }
        return(result.toString());
    }

    @TypeConverter
    public static Chore_Status fromIntcon(int value){
        return Chore_Status.values()[value];
    }

    @TypeConverter
    public static int fromStatus(Chore_Status value){
        return(value.ordinal());
    }
}
