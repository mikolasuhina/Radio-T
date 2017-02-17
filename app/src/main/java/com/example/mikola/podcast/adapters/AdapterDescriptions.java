package com.example.mikola.podcast.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mikola.podcast.views.CustomFontTextView;
import com.example.mikola.podcast.objs.Description;
import com.example.mikola.podcast.R;

import java.util.ArrayList;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterDescriptions extends BaseAdapter {

    ArrayList<Description> data;
    Context context;
    LayoutInflater layoutInflater;


    public AdapterDescriptions(ArrayList<Description> data, Context context) {
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


        Description itemDescription = data.get(position);
        if (itemDescription.getLogo() != null) {

            convertView = layoutInflater.inflate(R.layout.item_description_only_logo, null);
            ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
            logo.setImageBitmap(itemDescription.getLogo());

        } else {
            convertView = layoutInflater.inflate(R.layout.item_description, null);
            CustomFontTextView title = (CustomFontTextView) convertView.findViewById(R.id.text_post);
            if (itemDescription.getLinc() != null) {
                title.setTextColor(Color.BLUE);

                title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            if (itemDescription.getTime().length() > 0)
                title.setText(itemDescription.getText() + " - " + itemDescription.getTime());
            else {
                title.setText(itemDescription.getText());
            }

        }
        return convertView;
    }

}