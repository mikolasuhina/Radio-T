package com.example.mikola.podcast;

import android.support.v4.app.Fragment;


public class PodcastListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PodcastListFragment();
    }

}
