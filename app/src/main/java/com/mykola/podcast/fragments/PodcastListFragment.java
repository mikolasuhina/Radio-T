package com.mykola.podcast.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mykola.podcast.R;
import com.mykola.podcast.activitys.PodcastActivity;
import com.mykola.podcast.activitys.SettingsActivity;
import com.mykola.podcast.adapters.AdapterPodcasts;
import com.mykola.podcast.helpers.NetworkHelper;
import com.mykola.podcast.helpers.PodcastsManager;
import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.network.retrofit.APICallbacks;
import com.mykola.podcast.utils.TimeUtils;

import java.util.List;

import static com.mykola.podcast.fragments.PodcastFragment.PODCAST_TITLE;


public class PodcastListFragment extends SingleFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public final String TAG = getClass().getSimpleName();

    private RecyclerView listPodcasts;
    private AdapterPodcasts mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;
    private Toolbar mToolbar;
    private FloatingActionButton playingPodcastButton;

    private PodcastsManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        manager = PodcastsManager.getInstance();
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new AdapterPodcasts(manager.getPodcasts(), getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_podcast, container, false);

        initViews(view);
        setViewsParameters();

        refreshPodcasts();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    private void initViews(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listPodcasts = (RecyclerView) view.findViewById(R.id.list_podcasts);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        playingPodcastButton = (FloatingActionButton) view.findViewById(R.id.playing_podcast_btn);
    }

    private void setViewsParameters() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.BLUE);

        listPodcasts.setLayoutManager(mLayoutManager);
        listPodcasts.setAdapter(mAdapter);

        mToolbar.setTitle("");

        playingPodcastButton.setOnClickListener(this);

    }


    private void refreshPodcasts() {
        refreshLayout.setRefreshing(true);

        NetworkHelper.loadPodcasts(new APICallbacks<List<Podcast>>() {
            @Override
            public void onResponse(List<Podcast> response) {
                manager.updatePodcasts(response);

                refreshLayout.setRefreshing(false);

                Snackbar.make(mToolbar, getString(R.string.last_updated, TimeUtils.getCurrentDate()), Snackbar.LENGTH_LONG).show();

                updateUI();
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mToolbar, "error: " + t.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE).show();

                Log.d(TAG, "error: " + t.getMessage());

                refreshLayout.setRefreshing(false);
            }
        });

    }

    private void updateUI() {
        if (bound)
            if (musicService.getPodcast() == null) {
                playingPodcastButton.setVisibility(View.GONE);
            } else {
                playingPodcastButton.setVisibility(View.VISIBLE);
            }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void updatePlayerView() {

    }

    @Override
    protected void configurePlayerView() {
        mAdapter.setPlayingPodcast(musicService.getPodcast());
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onRefresh() {
        refreshPodcasts();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_podcasts_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_item:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.refresh_item:
                refreshPodcasts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_podcast_btn:
                Intent intent = new Intent(getContext(), PodcastActivity.class);
                intent.putExtra(PODCAST_TITLE, musicService.getPodcast().getTitle());
                startActivity(intent);
                break;
        }
    }
}
