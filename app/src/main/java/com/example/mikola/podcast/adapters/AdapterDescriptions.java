package com.example.mikola.podcast.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mikola.podcast.R;
import com.example.mikola.podcast.models.Description;
import com.example.mikola.podcast.views.CustomFontTextView;
import com.squareup.picasso.Picasso;

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
            Picasso.with(context).load(itemDescription.getLogo()).into(logo);

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


    public class PodcastHolder {
        public RelativeLayout layout;
        public CustomFontTextView title;
        public TextView date;
        public ImageView image;
        public ImageView playStatus;

        public PodcastHolder(View view) {
            layout = (RelativeLayout) view;
            title = (CustomFontTextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            image = (ImageView) view.findViewById(R.id.image);
            playStatus = (ImageView) view.findViewById(R.id.play_status);
        }
    }

}