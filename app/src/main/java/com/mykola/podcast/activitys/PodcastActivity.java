package com.mykola.podcast.activitys;

import android.support.v4.app.Fragment;

import com.mykola.podcast.fragments.PodcastFragment;

import static com.mykola.podcast.fragments.PodcastFragment.PODCAST_TITLE;


public class PodcastActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String title  =  getIntent().getStringExtra(PODCAST_TITLE);
        return PodcastFragment.newInstance(title);
    }
}
