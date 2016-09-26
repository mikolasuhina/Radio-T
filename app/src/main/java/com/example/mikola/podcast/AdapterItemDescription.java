package com.example.mikola.podcast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterItemDescription extends BaseAdapter {

    ArrayList <ItemDescriptionList> data;
    Context context;
    LayoutInflater layoutInflater;


    public AdapterItemDescription(ArrayList<ItemDescriptionList>data, Context context) {
        super();
        this.data = data;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemDescriptionList  podcast = data.get(position);
        convertView= layoutInflater.inflate(R.layout.item_description, null);

        TextView title=(TextView)convertView.findViewById(R.id.text_post);

        if(podcast.getLinc().length()>5){
            title.setTextColor(Color.BLUE);


           title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        if(podcast.getTime().length()>0)
        title.setText(podcast.getText()+" - "+podcast.getTime());
        else
        {title.setText(podcast.getText());
         }




        return convertView;
    }

}