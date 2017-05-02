package com.example.mikola.podcast.activitys;

import android.support.v4.app.Fragment;

import com.example.mikola.podcast.fragments.PodcastFragment;

import java.util.UUID;

import static com.example.mikola.podcast.fragments.PodcastFragment.PODCAST_ID;


public class PodcastActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        UUID id = (java.util.UUID) getIntent().getSerializableExtra(PODCAST_ID);
        return PodcastFragment.newInstance(id);
    }

}
