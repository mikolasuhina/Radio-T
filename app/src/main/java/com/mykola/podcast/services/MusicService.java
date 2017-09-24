package com.mykola.podcast.services;

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

import com.mykola.podcast.R;
import com.mykola.podcast.activitys.PodcastActivity;
import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static com.mykola.podcast.fragments.PodcastFragment.PODCAST_TITLE;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final String LOG_TAG = getClass().getSimpleName();

    private static final int NOTIFICATION_ID = 1;

    public static final String PLAY_ACTION = "foregroundservice.action.icon_play";
    public static final String STOPFOREGROUND_ACTION = "foregroundservice.action.stopforeground";

    public static final int SEND_UPDATE = 1;
    public static final int SEND_CONFIGURE = 2;

    public static final String ACTION_TYPE = "type";
    public static final String BROADCAST_ACTION = "SEND_DATA_ACTION";


    private PendingIntent pPlayIntent;
    private PendingIntent pCloseIntent;
    private PendingIntent pActivityIntent;

    private MediaPlayer mediaPlayer;

    private ThreadUpdate thread;

    public Podcast mPodcast;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(PLAY_ACTION);
        pPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);


        Intent closeIntent = new Intent(this, MusicService.class);
        closeIntent.setAction(STOPFOREGROUND_ACTION);
        pCloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        String action = intent.getAction();
        Log.d(LOG_TAG, "action = " + action);

        if (intent.getAction().equals(PLAY_ACTION)) {
            play();
        } else if (action.equals(STOPFOREGROUND_ACTION)) {
            onCompletion();
        }

        return START_NOT_STICKY;
    }


    public void play() {
        Log.d(LOG_TAG, "play()");
        Log.d(LOG_TAG, "play = " + isPlay());
        if (isPlay()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }

        updateNotification();
    }

    private void onCompletion() {

        resetPlayer();
        resetUpdateThread();

        sendBroadcast(SEND_CONFIGURE);

        stopForeground(true);
        stopSelf();

        mPodcast = null;
    }


    public void seekTo(int progres) {
        Log.d(LOG_TAG, "seekTo(int progres), progres = " + progres);
        mediaPlayer.seekTo(progres);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "onDestroy");
    }

    public boolean isPlayingPodcast(Podcast mPodcast) {
        if (this.mPodcast != null && mPodcast != null)
            if (this.mPodcast.equals(mPodcast))
                return true;

        return false;
    }

    public Podcast getPodcast() {
        return mPodcast;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlay() {
        return mediaPlayer.isPlaying();
    }


    public void init(Podcast mPodcast) {
        Log.d(LOG_TAG, "init(Podcast mPodcast)");


        this.mPodcast = mPodcast;

        initPendingIntent();

        initUpdateThread();

        resetPlayer();
        initPlayer(mPodcast);
    }

    private void initPendingIntent() {
        Log.d(LOG_TAG, "initPendingIntent");

        Intent notificationIntent = new Intent(this, PodcastActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(PODCAST_TITLE, mPodcast.getTitle());

        pActivityIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void initUpdateThread() {
        Log.d(LOG_TAG, "initUpdateThread");

        thread = new ThreadUpdate();
        thread.start();
    }

    private void resetUpdateThread() {
        Log.d(LOG_TAG, "resetUpdateThread");
        thread.setRunning(false);
    }

    private void resetPlayer() {
        Log.d(LOG_TAG, "resetPlayer");
        mediaPlayer.reset();
    }

    private void initPlayer(Podcast podcast) {
        Log.d(LOG_TAG, "initPlayer");

        try {
            mediaPlayer.setDataSource(podcast.getSound());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showNotification() {

        Notification notification = buildNotification();

        Log.d(LOG_TAG, "startForeground");
        startForeground(NOTIFICATION_ID, notification);
    }

    private void updateNotification() {
        Notification notification = buildNotification();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification() {

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(pActivityIntent)
                .setSmallIcon(isPlay() ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_play)
                .setPriority(Notification.PRIORITY_MAX);

        RemoteViews notificationView = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationView = getComplexNotificationView();
            builder.setCustomBigContentView(notificationView);
        } else {
            builder.setContentTitle(mPodcast.getTitle())
                    .setContentText(TimeUtils.getTimeString(getCurrentPosition()) + "/" + TimeUtils.getTimeString(getDuration()));
        }

        Notification notification = builder.build();

        if (notificationView != null)
            Picasso.with(this)
                    .load(mPodcast.getImage())
                    .into(notificationView, R.id.status_bar_album_art, NOTIFICATION_ID, notification);

        return notification;
    }

    private RemoteViews getComplexNotificationView() {
        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.player_notification_view);

        if (isPlay())
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_pause);
        else
            notificationView.setImageViewResource(R.id.status_bar_play, android.R.drawable.ic_media_play);


        notificationView.setTextViewText(R.id.status_bar_track_name, mPodcast.getTitle());
        notificationView.setImageViewResource(R.id.status_bar_collapse, android.R.drawable.ic_menu_close_clear_cancel);

        notificationView.setOnClickPendingIntent(R.id.status_bar_collapse, pCloseIntent);
        notificationView.setOnClickPendingIntent(R.id.status_bar_play, pPlayIntent);

        return notificationView;

    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        MyBinder binder = new MyBinder();

        Log.d(LOG_TAG, "onBind");
        return binder;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG, "onPrepared(MediaPlayer mediaPlayer)");
        mediaPlayer.start();

        showNotification();

        sendBroadcast(SEND_CONFIGURE);
    }

    @Override
    public void onCompletion(MediaPlayer mMediaPlayer) {
        Log.d(LOG_TAG, "onCompletion");
        onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mMediaPlayer, int what, int extra) {
        Log.d(LOG_TAG, "what = " + what + ", extra = " + extra);
        return true;
    }


    private class ThreadUpdate extends Thread {
        private boolean running;

        ThreadUpdate() {
            this.running = true;
        }

        boolean isRunning() {
            return running;
        }

        void setRunning(boolean mRunning) {
            running = mRunning;
        }


        @Override
        public void run() {
            Log.i(LOG_TAG, "thread start");
            while (running) {
                if (isRunning()) {
                    sendBroadcast(SEND_UPDATE);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(LOG_TAG, "thread stop");

        }
    }

    public class MyBinder extends Binder {

        public MusicService getService() {
            sendBroadcast(SEND_CONFIGURE);
            sendBroadcast(SEND_UPDATE);
            return MusicService.this;
        }
    }


    private void sendBroadcast(int type) {
        Log.d(LOG_TAG, "sendBroadcast: type = " + type);
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(ACTION_TYPE, type);
        sendBroadcast(intent);
    }

}
