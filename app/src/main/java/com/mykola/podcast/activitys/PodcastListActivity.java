package com.mykola.podcast.activitys;

import android.support.v4.app.Fragment;

import com.mykola.podcast.fragments.PodcastListFragment;


public class PodcastListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PodcastListFragment();
    }

}
