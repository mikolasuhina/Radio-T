package com.example.mikola.podcast.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikola.podcast.R;
import com.example.mikola.podcast.adapters.AdapterPodcasts;
import com.example.mikola.podcast.helpers.PodcastsManager;
import com.example.mikola.podcast.helpers.PreferenceHelper;
import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.network.DataLoader;
import com.example.mikola.podcast.services.LoadService;
import com.example.mikola.podcast.services.MusicService;
import com.example.mikola.podcast.utils.Constants;

import java.util.ArrayList;

/**
 * Created by mykola on 08.02.17.
 */

public class PodcastListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView listOfPodcast;
    private SwipeRefreshLayout refreshLayout;
    private ImageView statusImg;
    private TextView statusText;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwitchCompat synchronStatus;

    private Animation animation;

    private PodcastsManager manager;

    private String TAG = "TAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_podcast, container, false);
        init(view);
        setParam();

        updateUI();
        return view;
    }

    private void init(View view) {

        manager = PodcastsManager.getInstance(getContext());

        statusImg = (ImageView) view.findViewById(R.id.status_img);
        statusText = (TextView) view.findViewById(R.id.status_text);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listOfPodcast = (RecyclerView) view.findViewById(R.id.list_podcasts);
        synchronStatus = (SwitchCompat) view.findViewById(R.id.synchron_status);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);

        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new AdapterPodcasts(manager.getPodcasts(), getActivity());
    }

    private void setParam() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.BLUE);

        listOfPodcast.setLayoutManager(mLayoutManager);
        listOfPodcast.setAdapter(mAdapter);
        synchronStatus.setChecked(PreferenceHelper.getPrefIsAlarmOn(getContext()));
        synchronStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                LoadService.setServiceAlarm(getContext(), b);

            }
        });
    }

    private void updateUI() {

        if (!MusicService.isRunning()) {
            new RequestTask().execute();
        } else {
            listOfPodcast.setVisibility(View.VISIBLE);
            statusImg.clearAnimation();
            statusImg.setImageResource(R.drawable.icon_done);
            statusText.setText(R.string.synchronize_complete);

        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        new RequestTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.status_text: {
                new RequestTask().execute();
                break;
            }
        }
    }

    private class RequestTask extends AsyncTask<String, String, ArrayList<Podcast>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusImg.setImageResource(R.drawable.icon_loop);
            statusImg.startAnimation(animation);
            statusText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            statusText.setText(R.string.synchronize);
        }


        @Override
        protected ArrayList<Podcast> doInBackground(String... uri) {
            return new DataLoader(getContext()).loadPodcasts();

        }

        @Override
        protected void onPostExecute(ArrayList<Podcast> result) {
            super.onPostExecute(result);
            if (result == null) {
                statusText.setText(R.string.error_text);
                statusText.setTextColor(getResources().getColor(R.color.colorAccent));
                statusImg.setImageResource(R.drawable.icon_error);
            } else {
                manager.addAllPodcast(result);
                statusText.setText(R.string.synchronize_complete);
                statusImg.setImageResource(R.drawable.icon_done);
            }

            refreshLayout.setRefreshing(false);
            listOfPodcast.setVisibility(View.VISIBLE);
            statusImg.clearAnimation();
            mAdapter.notifyDataSetChanged();

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!MusicService.isRunning()) {
            Intent startIntent = new Intent(getActivity(), MusicService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            getActivity().startService(startIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!MusicService.isStarted()) {
            Intent stopIntent = new Intent(getActivity(), MusicService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            getActivity().stopService(stopIntent);
        }
    }


}
