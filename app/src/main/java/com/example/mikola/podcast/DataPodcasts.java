package com.example.mikola.podcast;

import android.content.Context;

import com.example.mikola.podcast.objs.Podcast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mykola on 08.02.17.
 */
public class DataPodcasts {
    private static DataPodcasts sData;
    private List<Podcast> podcasts;

    public static DataPodcasts getInstance(Context context) {
        if (sData == null)
            sData = new DataPodcasts(context);

        return sData;
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    private DataPodcasts(Context context) {
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
