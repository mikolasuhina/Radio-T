package com.mykola.podcast.helpers;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.network.retrofit.APICallbacks;
import com.mykola.podcast.network.retrofit.ServiceBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkHelper {

    public static void loadPodcasts(final APICallbacks<List<Podcast>> callback) {
        ServiceBuilder.getApi().getPodcasts().enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Call<List<Podcast>> call, Response<List<Podcast>> response) {

                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<Podcast>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public static void downloadSound(Context context, Podcast podcast) {

        Uri uri = Uri.parse(podcast.getSound());

        DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(podcast.getTitle())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, podcast.getTitle() + ".mp3");

        mDownloadManager.enqueue(request);
    }
}
