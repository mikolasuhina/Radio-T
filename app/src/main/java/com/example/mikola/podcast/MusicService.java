package com.example.mikola.podcast;

import android.annotation.TargetApi;
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

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mikola on 22.09.2016.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    private Podcast podcast;

    RemoteViews notificationView;
    Intent playIntent;
    PendingIntent pplayIntent;
    PendingIntent pnextIntent;
    Intent notificationIntent;
    PendingIntent pendingIntent;
    PendingIntent ppreviousIntent;
    PendingIntent pcloseIntent;
    private MediaPlayer mediaPlayer;
    public static boolean running;
    public static boolean playing;
    public static UUID id;
    private static final String LOG_TAG = "TAG";


    public static boolean isRunning() {
        return running;
    }

    public static boolean isPlaying() {
        return playing;
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "onStartCommand");
        if (intent.getAction().equals(Constans.ACTION.STARTFOREGROUND_ACTION)) {
            Log.d(LOG_TAG, "Received Start Foreground Intent ");
            notificationIntent = new Intent(this, PodcastListActivity.class);
            notificationIntent.setAction(Constans.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this,
                    Constans.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(Constans.ACTION.PLAY_ACTION);
            pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, 0);


            Intent closeIntent = new Intent(this, MusicService.class);
            closeIntent.setAction(Constans.ACTION.STOPFOREGROUND_ACTION);
            pcloseIntent = PendingIntent.getService(this, 0,
                    closeIntent, 0);


            setRunning(true);


        } else if (intent.getAction().equals(Constans.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constans.ACTION.PLAY_ACTION)) {

            play(podcast);
            update();

            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constans.ACTION.NEXT_ACTION)) {

            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(
                Constans.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            mediaPlayer.stop();
            stopForeground(true);
            stopSelf();
            System.exit(0);
        }
        return START_NOT_STICKY;

    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        playing = mediaPlayer.isPlaying();
        update();
    }


    public static UUID getId() {
        return id;
    }

    public int getLenghtSound() {
        if (mediaPlayer != null)
            return mediaPlayer.getDuration();
        else return 0;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        else return 0;
    }


    public void play(Podcast podcast) {

        if (!podcast.getId().equals(this.podcast.getTitle())) {
            this.podcast = podcast;
            initPlayer(podcast);
            showNotification();
        }

        if (isPlaying()) {
            mediaPlayer.pause();
            playing = false;
        } else {
            mediaPlayer.start();
            playing = true;
        }
        update();

    }


    public void seekTo(int progres) {
        mediaPlayer.seekTo(progres);
    }


    private RemoteViews getComplexNotificationView() {
        // Using RemoteViews to bind notification_view layouts into Notification
        notificationView = new RemoteViews(
                this.getPackageName(),
                R.layout.notification_view
        );
        // Locate and set the Image into customnotificationtext.xml ImageViews
        if (isPlaying())
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_pause);
        else
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_play);


        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.status_bar_track_name, podcast.getTitle());
        notificationView.setTextViewText(R.id.text, "");
        notificationView.setImageViewBitmap(R.id.status_bar_album_art, podcast.getImage());

        notificationView.setImageViewResource(R.id.status_bar_collapse, android.R.drawable.ic_menu_close_clear_cancel);

        notificationView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        notificationView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        return notificationView;

    }

    private void showNotification() {
        startForeground(Constans.NOTIFICATION_ID.FOREGROUND_SERVICE,
                buildNotification().build());
    }

    private void update() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Constans.NOTIFICATION_ID.FOREGROUND_SERVICE, buildNotification().build());
    }

    protected NotificationCompat.Builder buildNotification() {


        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pendingIntent);
        if (isPlaying())
            builder.setSmallIcon(android.R.drawable.ic_media_play);
        else
            builder.setSmallIcon(android.R.drawable.ic_media_pause);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = (NotificationCompat.Builder) builder.setCustomBigContentView(getComplexNotificationView());
            builder.setPriority(Notification.PRIORITY_MAX);
        } else {
            // Build a simpler notification, without buttons
            //
            builder = (NotificationCompat.Builder) builder.setContentTitle(podcast.getTitle())
                    .setContentText(podcast.getTitle())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(android.R.drawable.ic_menu_gallery);
        }
        return builder;
    }

    class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
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
}
