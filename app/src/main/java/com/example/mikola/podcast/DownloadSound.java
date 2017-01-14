package com.example.mikola.podcast;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;

/**
 * Created by mikola on 14.11.2016.
 */

public class DownloadSound {

    private DownloadManager mDownloadManager;
    private long myDownloadRefference;
    private BroadcastReceiver receiverDownloadComplete;

    private Context mContext;

    private String downloadURL;


    public DownloadSound(Context context, final Podcast podcast) {


        Toast.makeText(context, context.getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();

        downloadURL = podcast.getSound();
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadURL);
        final DownloadManager.Request request = new DownloadManager.Request(uri);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, podcast.getTitle() + ".mp3");

        } else {
            return;
        }
        //обчислення розміру файла
        new Thread(new Runnable() {
            @Override
            public void run() {

                myDownloadRefference = mDownloadManager.enqueue(request);
            }
        }).start();


        request.setTitle(podcast.getTitle())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);


        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadRefference == reference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);

                    Cursor cursor = mDownloadManager.query(query);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);


                }
            }

        };
        mContext.registerReceiver(receiverDownloadComplete, intentFilter);
    }

}



