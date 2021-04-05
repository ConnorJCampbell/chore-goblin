package com.example.choregoblin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.Data.Goblin;
import com.example.choregoblin.R;

import java.util.ArrayList;

public class GoblinListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resource;
    private ArrayList<Goblin> data;

    public GoblinListAdapter(Context context, int resource, ArrayList<Goblin> data){
        this.inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        this.resource = resource;
        this.data = data;
    }

    public int getCount(){
        return this.data.size();
    }

    public Object getItem(int position){
        return this.data.get(position);
    }
    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        if (convertView == null){
            view = this.inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        return this.bindData(view, position);
    }
    public View bindData(View view, int position){
        if (this.data.get(position) == null){
            return view;
        }

        String tempString;

        Goblin item = this.data.get(position);
        View viewElement = view.findViewById(R.id.goblin_list_item_name);
        TextView textView = (TextView)viewElement;
        tempString = "Goblin: " + item.getGoblinName();
        textView.setText(tempString);
        viewElement = view.findViewById(R.id.goblin_list_item_owner);
        textView = (TextView)viewElement;
        tempString = "Goblin Owner: " + item.getGoblinOwner();
        textView.setText(tempString);
        return(view);
    }
    public void setList(ArrayList<Goblin> newList){
        this.data = newList;
    }

}
