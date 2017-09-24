package com.mykola.podcast.helpers;

import com.mykola.podcast.models.Podcast;

import java.util.ArrayList;
import java.util.List;

public class PodcastsManager {
    private static PodcastsManager sData;
    private List<Podcast> podcasts;

    public static PodcastsManager getInstance() {
        if (sData == null)
            sData = new PodcastsManager();

        return sData;
    }


    private PodcastsManager() {
        podcasts = new ArrayList<>();
    }

    public Podcast getPodcast(String title) {
        for (Podcast podcast : podcasts) {
            if (podcast.getTitle().equals(title)) {
                return podcast;
            }

        }
        return null;
    }

    public void updatePodcasts(List<Podcast> podcasts) {
        this.podcasts.clear();
        this.podcasts.addAll(podcasts);
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }
}
