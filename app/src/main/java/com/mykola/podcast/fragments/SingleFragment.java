package com.mykola.podcast.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.mykola.podcast.services.MusicService;

import static com.mykola.podcast.services.MusicService.ACTION_TYPE;
import static com.mykola.podcast.services.MusicService.BROADCAST_ACTION;
import static com.mykola.podcast.services.MusicService.SEND_CONFIGURE;
import static com.mykola.podcast.services.MusicService.SEND_UPDATE;


public abstract class SingleFragment extends Fragment {

    protected MusicService musicService;
    protected boolean bound;

    protected ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private BroadcastReceiver br = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(ACTION_TYPE, 0);
            switch (type) {
                case SEND_CONFIGURE:
                    configurePlayerView();
                    break;
                case SEND_UPDATE:
                    updatePlayerView();
                    break;
            }

        }
    };

    protected abstract void updatePlayerView();

    protected abstract void configurePlayerView();

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        getContext().registerReceiver(br, intFilt);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(br);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            getActivity().unbindService(mConnection);
            bound = false;
        }
    }

}
