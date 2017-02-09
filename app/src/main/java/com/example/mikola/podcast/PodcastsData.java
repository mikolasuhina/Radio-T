package com.example.mikola.podcast;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mykola on 08.02.17.
 */
public class PodcastsData {
    private static PodcastsData sData;
    private List<Podcast> podcasts;

    public static PodcastsData getInstance(Context context) {
        if (sData == null)
            sData = new PodcastsData(context);

        return sData;
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    private PodcastsData(Context context) {
       podcasts = new ArrayList<>();

    }
    public Podcast getPodcast(UUID id){
        for (Podcast podcast:podcasts) {
            if(podcast.getId().equals(id)) {
                return podcast;
            }

        }
        return null;
    }
    public Podcast getPodcast(String title){
        for (Podcast podcast:podcasts) {
            if(podcast.getTitle().equals(title)) {
                return podcast;
            }

        }
        return null;
    }

    public void addPodcast(Podcast podcast){
        podcasts.add(podcast);
    }
}
