package com.example.mikola.podcast.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mikola.podcast.R;
import com.example.mikola.podcast.activitys.PodcastListActivity;
import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static com.example.mikola.podcast.utils.Constants.ACTION_TYPE;
import static com.example.mikola.podcast.utils.Constants.BROADCAST_ACTION;
import static com.example.mikola.podcast.utils.Constants.SEND_DATA;
import static com.example.mikola.podcast.utils.Constants.SEND_DURATION;

/**
 * Created by mikola on 22.09.2016.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    private RemoteViews notificationView;

    PendingIntent pplayIntent;
    PendingIntent pendingIntent;
    PendingIntent pcloseIntent;

    private MediaPlayer mediaPlayer;
    public static boolean running;
    public static Podcast sPodcast;
    private static final String LOG_TAG = "TAG_SERVICE";
    private Intent intent = new Intent(BROADCAST_ACTION);
    private ThreadUpdate thread;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "onStartCommand");
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            thread = new ThreadUpdate();
            thread.setStarted(true);
            thread.start();

            Log.d(LOG_TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, PodcastListActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this,
                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            Intent playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, 0);


            Intent closeIntent = new Intent(this, MusicService.class);
            closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            pcloseIntent = PendingIntent.getService(this, 0,
                    closeIntent, 0);

            setRunning(true);

        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {

            play(sPodcast);
            updateNotification();

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            sPodcast.setPlaying(false);
            mediaPlayer.stop();
            thread.setStarted(false);
            setRunning(false);
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;

    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        updateNotification();
        sendData();
        sendDuration();
    }


    public int getDuration(Podcast podcast) {
        if (mediaPlayer != null && podcast == sPodcast)
            return mediaPlayer.getDuration();
        else return 0;
    }

    public int getCurrentPosition(Podcast podcast) {
        if (mediaPlayer != null && podcast == sPodcast)
            return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public boolean isPlay(Podcast podcast) {
        if (mediaPlayer != null && podcast == sPodcast)
            return mediaPlayer.isPlaying();
        else return false;
    }

    public void play(Podcast podcast) {

        if (podcast != sPodcast) {
            if (sPodcast != null)
                sPodcast.setPlaying(false);
            sPodcast = podcast;
            sPodcast.setPlaying(true);

            initPlayer(sPodcast);
            showNotification();
        }

        if (isPlay(sPodcast)) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        updateNotification();
    }


    public void seekTo(int progres, Podcast podcast) {
        if (podcast == sPodcast)
            mediaPlayer.seekTo(progres);
    }


    private RemoteViews getComplexNotificationView() {
        notificationView = new RemoteViews(
                this.getPackageName(),
                R.layout.notification_view);

        if (isPlay(sPodcast))
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_pause);
        else
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_play);


        notificationView.setTextViewText(R.id.status_bar_track_name, sPodcast.getTitle());
        notificationView.setImageViewResource(R.id.status_bar_collapse, android.R.drawable.ic_menu_close_clear_cancel);

        notificationView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        notificationView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        return notificationView;

    }

    private void showNotification() {
        Notification notification = buildNotification().build();
        Picasso.with(this).load(sPodcast.getImage())
                .into(notificationView, R.id.status_bar_album_art,
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void updateNotification() {
        Notification notification = buildNotification().build();
        Picasso.with(this).load(sPodcast.getImage())
                .into(notificationView, R.id.status_bar_album_art,
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    protected NotificationCompat.Builder buildNotification() {

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (isPlay(sPodcast))
            builder.setSmallIcon(android.R.drawable.ic_media_play);
        else
            builder.setSmallIcon(android.R.drawable.ic_media_pause);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = (NotificationCompat.Builder) builder.setCustomBigContentView(getComplexNotificationView());
            builder.setPriority(Notification.PRIORITY_MAX);
        } else {
            builder = (NotificationCompat.Builder) builder.setContentTitle(sPodcast.getTitle())
                    .setContentText(sPodcast.getTitle())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(android.R.drawable.ic_menu_gallery);
        }

        return builder;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            sendDuration();
            sendData();
            return MusicService.this;
        }
    }


    public static boolean isRunning() {
        return running;
    }

    public static boolean isStarted() {
        if (sPodcast != null) return true;
        return false;
    }


    public void setRunning(boolean running) {
        this.running = running;
    }


    private void initPlayer(Podcast podcast) {
        if (mediaPlayer != null)
            mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(podcast.getSound());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        MyBinder binder = new MyBinder();

        Log.i(LOG_TAG, "onBind");
        return binder;
    }


    private void sendData() {
        intent.putExtra(ACTION_TYPE, SEND_DATA);
        sendBroadcast(intent);
    }

    private void sendDuration() {
        intent.putExtra(ACTION_TYPE, SEND_DURATION);
        sendBroadcast(intent);
    }


    private class ThreadUpdate extends Thread {
        private boolean started;

        public boolean isStarted() {
            return started;
        }

        public void setStarted(boolean started) {
            this.started = started;
        }

        @Override
        public void run() {
            Log.i(LOG_TAG, "thread start");
            while (started) {
                if (isStarted())
                    sendData();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(LOG_TAG, "thread stop");

        }
    }

}
