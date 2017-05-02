package com.example.mikola.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mikola.podcast.activitys.PodcastActivity;
import com.example.mikola.podcast.R;
import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.views.CustomFontTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.mikola.podcast.fragments.PodcastFragment.PODCAST_ID;

/**
 * Created by mikola on 21.09.2016.
 */

public class AdapterPodcasts extends RecyclerView.Adapter<AdapterPodcasts.ViewHolder>{

    private List<Podcast> podcasts;
    private Context context;



    public AdapterPodcasts(List<Podcast> podcasts, Context context) {
        super();
        this.podcasts = podcasts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcastst_right, null);

       // Animation animation = AnimationUtils.loadAnimation(context, R.anim.left);
       // convertView.startAnimation(animation);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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


    @Override
    public int getItemCount() {
        return podcasts.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout layout;
        public CustomFontTextView title;
        public TextView date;
        public ImageView image;
        public ImageView playStatus;

        public ViewHolder(View view) {
            super(view);
            layout = (RelativeLayout) view;
            title = (CustomFontTextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            image = (ImageView) view.findViewById(R.id.image);
            playStatus = (ImageView) view.findViewById(R.id.play_status);
        }


    }

}