package com.example.mikola.podcast.activitys;

import android.support.v4.app.Fragment;

import com.example.mikola.podcast.fragments.PodcastListFragment;


public class PodcastListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PodcastListFragment();
    }

}
