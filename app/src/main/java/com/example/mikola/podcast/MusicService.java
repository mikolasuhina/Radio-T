package com.example.mikola.podcast;

import android.annotation.TargetApi;
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

/**
 * Created by mikola on 22.09.2016.
 */

public class MusicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        MyBinder binder = new MyBinder();
        Log.i(LOG_TAG, "onBind");
        return binder;
    }

    PodcastItem podcast;

    RemoteViews notificationView;
    Intent playIntent;
    PendingIntent pplayIntent;
    PendingIntent pnextIntent;
    Intent notificationIntent;
    PendingIntent pendingIntent;
    PendingIntent ppreviousIntent;
    PendingIntent pcloseIntent;
    MediaPlayer mediaPlayer;
    static int usePosPodcastFromList = -1;
    public static boolean running;
    public static boolean startFore;

    public static boolean isStartFore() {
        return startFore;
    }

    public static void setStartFore(boolean startFore) {
        MusicService.startFore = startFore;
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static void setPlaying(boolean playing) {
        MusicService.playing = playing;
    }

    public static boolean playing;
    private static final String LOG_TAG = "myTag";

    public static boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    void initPlayer() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(MainActivity.thisPodcast.getSound());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(LOG_TAG, "onStartCommand");
        if (intent.getAction().equals(Constans.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            notificationIntent = new Intent(this,MainActivity.class);
            notificationIntent.setAction(Constans.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity( this,
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

            if (isplaing())
                mediaPlayer.pause();
            else mediaPlayer.start();
            update();

            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constans.ACTION.NEXT_ACTION)) {

            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(
                Constans.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            mediaPlayer.stop();
            stopForeground(true)
            ;stopSelf();
            System.exit(0);



        }
        return START_NOT_STICKY;

    }

    void showNotification() {
        startForeground(Constans.NOTIFICATION_ID.FOREGROUND_SERVICE,
                buildNotification().build());
    }

    class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void update() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Constans.NOTIFICATION_ID.FOREGROUND_SERVICE, buildNotification().build());
    }

    boolean isplaing() {

        if (mediaPlayer != null)
            return mediaPlayer.isPlaying();
        else return false;
    }

    int getLenghtSound() {
        if(mediaPlayer!=null)
        return mediaPlayer.getDuration();
        else return 0;
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    boolean play_pause() {

        playing=true;

        if (MainActivity.pos != usePosPodcastFromList) {
            usePosPodcastFromList = MainActivity.pos;
            initPlayer();
            MainActivity.thisPodcast.setPlaying(true);
            podcast=MainActivity.thisPodcast;

        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if(startFore)
            update();
            return false;
        } else {
            mediaPlayer.start();
            if(startFore)
            update();
            return true;
        }


    }



    void seekTo(int progres) {
        mediaPlayer.seekTo(progres);
    }


    private RemoteViews getComplexNotificationView() {
        // Using RemoteViews to bind notification_view layouts into Notification
        notificationView = new RemoteViews(
                this.getPackageName(),
                R.layout.notification_view
        );
        // Locate and set the Image into customnotificationtext.xml ImageViews
        if (isplaing())
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_pause);
        else
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_play);


        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.status_bar_track_name, podcast.getTitle());
        notificationView.setTextViewText(R.id.text, "jewmdsfio");
        notificationView.setImageViewBitmap(R.id.status_bar_album_art, podcast.getImage());

        notificationView.setImageViewResource(R.id.status_bar_collapse, android.R.drawable.ic_menu_close_clear_cancel);

        notificationView.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        notificationView.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        return notificationView;

    }

    protected NotificationCompat.Builder buildNotification() {



        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pendingIntent);
        if (isplaing())
            builder.setSmallIcon(android.R.drawable.ic_media_play);
        else
            builder.setSmallIcon(android.R.drawable.ic_media_pause);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = (NotificationCompat.Builder) builder.setCustomBigContentView(getComplexNotificationView());
        } else {
            // Build a simpler notification, without buttons
            //
            builder = (NotificationCompat.Builder) builder.setContentTitle(podcast.getTitle())
                    .setContentText(podcast.getTitle())
                    .setSmallIcon(android.R.drawable.ic_menu_gallery);
        }
        return builder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);

    }
}
