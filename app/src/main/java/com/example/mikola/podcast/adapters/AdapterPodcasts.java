package com.example.mikola.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikola.podcast.views.CustomFontTextView;
import com.example.mikola.podcast.objs.Podcast;
import com.example.mikola.podcast.PodcastActivity;
import com.example.mikola.podcast.R;

import java.util.List;

import static com.example.mikola.podcast.PodcastFragment.PODCAST_ID;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterPodcasts extends BaseAdapter {

    private List<Podcast> data;
    private Context context;
    private LayoutInflater layoutInflater;


    public AdapterPodcasts(List<Podcast> data, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Podcast podcast = data.get(position);
        convertView = layoutInflater.inflate(R.layout.item_podcastst_right, null);
        CustomFontTextView title = (CustomFontTextView) convertView.findViewById(R.id.title);
        TextView data = (TextView) convertView.findViewById(R.id.data);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        ImageView useItem = (ImageView) convertView.findViewById(R.id.useItem);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PodcastActivity.class);
                intent.putExtra(PODCAST_ID, podcast.getId());
                context.startActivity(intent);
            }
        });

        title.setText(podcast.getTitle());
        data.setText(podcast.getData());
        image.setImageBitmap(podcast.getImage());

        if (podcast.isPlaying()) {
            convertView.setBackgroundResource(R.color.colorPrimary);
            useItem.setBackgroundResource(R.drawable.speaker_animation);
            AnimationDrawable animation = (AnimationDrawable) useItem.getBackground();
            animation.start();
        }


        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left);
        convertView.startAnimation(animation);


        return convertView;
    }

}