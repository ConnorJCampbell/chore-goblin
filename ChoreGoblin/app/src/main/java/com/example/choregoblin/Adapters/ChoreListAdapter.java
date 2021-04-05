package com.example.choregoblin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.choregoblin.Data.Chore;
import com.example.choregoblin.R;

import java.util.ArrayList;

public class ChoreListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resource;
    private ArrayList<Chore> data;

    public ChoreListAdapter(Context context, int resource, ArrayList<Chore> data){
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

        Chore item = this.data.get(position);
        View viewElement = view.findViewById(R.id.title);
        TextView textView = (TextView)viewElement;
        textView.setText(item.getTitle());

        viewElement = view.findViewById(R.id.effort_value);
        textView = (TextView)viewElement;

        tempString = "XP: "+ item.getEffortValue();
        Log.d("Effort: ", tempString);
        textView.setText(tempString);
        return(view);
    }
    public void setList(ArrayList<Chore> newList){
        this.data = newList;
    }

}
