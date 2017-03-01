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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mikola.podcast.PodcastActivity;
import com.example.mikola.podcast.R;
import com.example.mikola.podcast.objs.Podcast;
import com.example.mikola.podcast.views.CustomFontTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.mikola.podcast.PodcastFragment.PODCAST_ID;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterPodcasts extends BaseAdapter {

    private List<Podcast> podcasts;
    private Context context;
    private LayoutInflater layoutInflater;


    public AdapterPodcasts(List<Podcast> podcasts, Context context) {
        super();
        this.podcasts = podcasts;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return podcasts.size();
    }

    @Override
    public Object getItem(int position) {
        return podcasts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.item_podcastst_right, null);

        PodcastHolder podcastHolder = new PodcastHolder(convertView);
        onBindHolder(podcastHolder, position);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left);
        convertView.startAnimation(animation);

        return convertView;
    }


    public void onBindHolder(PodcastHolder holder, int position) {
        final Podcast podcast = podcasts.get(position);
        holder.title.setText(podcast.getTitle());
        holder.date.setText(podcast.getData());
       // holder.image.setImageBitmap(podcast.getImage());
        Picasso.with(context).load(podcast.getImage()).into(holder.image);
        if (podcast.isPlaying()) {
            holder.layout.setBackgroundResource(R.color.colorPrimary);
            holder.playStatus.setBackgroundResource(R.drawable.speaker_animation);
            AnimationDrawable animation = (AnimationDrawable) holder.playStatus.getBackground();
            animation.start();
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PodcastActivity.class);
                intent.putExtra(PODCAST_ID, podcast.getId());
                context.startActivity(intent);
            }
        });

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