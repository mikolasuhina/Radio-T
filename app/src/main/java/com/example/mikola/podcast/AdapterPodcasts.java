package com.example.mikola.podcast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterPodcasts extends BaseAdapter {

    ArrayList <Podcast> data;
    Context context;
    LayoutInflater layoutInflater;


    public AdapterPodcasts(ArrayList<Podcast>data, Context context) {
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

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Podcast podcast = data.get(position);
        if(position%2==0)
        convertView= layoutInflater.inflate(R.layout.item, null);
        else  convertView= layoutInflater.inflate(R.layout.item, null);

        TextView title=(TextView)convertView.findViewById(R.id.title);
        TextView data=(TextView)convertView.findViewById(R.id.data);
        ImageView image =(ImageView)convertView.findViewById(R.id.image) ;
        ImageView useItem =(ImageView)convertView.findViewById(R.id.useItem) ;

        title.setText(podcast.getTitle());
        data.setText(podcast.getData());
        image.setImageBitmap(podcast.getImage());
        if(podcast.isPlaying()){
            convertView.setBackgroundResource(R.color.coloruse);
            useItem.setBackgroundResource(R.drawable.animsound);
         AnimationDrawable animation =(AnimationDrawable)useItem.getBackground();
        animation.start();
        }


            Animation animation = AnimationUtils.loadAnimation(context, R.anim.left);
            convertView.startAnimation(animation);



        return convertView;
    }

}