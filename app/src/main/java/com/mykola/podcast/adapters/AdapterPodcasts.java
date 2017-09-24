package com.mykola.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykola.podcast.R;
import com.mykola.podcast.activitys.PodcastActivity;
import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.views.CustomFontTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mykola.podcast.fragments.PodcastFragment.PODCAST_TITLE;


public class AdapterPodcasts extends RecyclerView.Adapter<AdapterPodcasts.ViewHolder> {

    private List<Podcast> podcasts;
    private Context context;

    private Podcast playingPodcast;

    public AdapterPodcasts(List<Podcast> podcasts, Context context) {
        this.podcasts = podcasts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast, null);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Podcast podcast = podcasts.get(position);

        holder.title.setText(podcast.getTitle());
        holder.date.setText(podcast.getDate());

        Picasso.with(context).load(podcast.getImage()).into(holder.image);

        if (playingPodcast != null && podcast.equals(playingPodcast)) {
            holder.layout.setCardBackgroundColor(Color.parseColor("#E0EEEE"));
            holder.playStatus.setVisibility(View.VISIBLE);
        } else {
            holder.layout.setCardBackgroundColor(Color.WHITE);
            holder.playStatus.setVisibility(View.INVISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PodcastActivity.class);
                intent.putExtra(PODCAST_TITLE, podcast.getTitle());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return podcasts.size();
    }


   class ViewHolder extends RecyclerView.ViewHolder {

         CardView layout;
         TextView title;
         TextView date;
         ImageView image;
         ImageView playStatus;

         ViewHolder(View view) {
            super(view);
            layout = (CardView) view;
            title = (CustomFontTextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            image = (ImageView) view.findViewById(R.id.image);
            playStatus = (ImageView) view.findViewById(R.id.play_status);
        }
    }

    public void setPlayingPodcast(Podcast mPlayingPodcast) {
        playingPodcast = mPlayingPodcast;
    }
}