package com.example.mikola.podcast.helpers;

import android.content.Context;

import com.example.mikola.podcast.models.Podcast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mykola on 08.02.17.
 */
public class PodcastsManager {
    private static PodcastsManager sData;
    private List<Podcast> podcasts;

    public static PodcastsManager getInstance(Context context) {
        if (sData == null)
            sData = new PodcastsManager(context);

        return sData;
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    private PodcastsManager(Context context) {
        podcasts = new ArrayList<>();

    }

    public Podcast getPodcast(UUID id) {
        for (Podcast podcast : podcasts) {
            if (podcast.getId().equals(id)) {
                return podcast;
            }

        }
        return null;
    }

    public Podcast getPodcast(String title) {
        for (Podcast podcast : podcasts) {
            if (podcast.getTitle().equals(title)) {
                return podcast;
            }

        }
        return null;
    }

    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }

    public void addAllPodcast(ArrayList<Podcast> podcasts) {
        this.podcasts.addAll(podcasts);
    }
}
