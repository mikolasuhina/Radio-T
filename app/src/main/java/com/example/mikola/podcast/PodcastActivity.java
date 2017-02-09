package com.example.mikola.podcast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import java.util.UUID;

import static com.example.mikola.podcast.PodcastFragment.PODCAST_ID;


public class PodcastActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        UUID id = (java.util.UUID) getIntent().getSerializableExtra(PODCAST_ID);
        return PodcastFragment.newInstance(id);
    }


}
