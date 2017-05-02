package com.example.mikola.podcast.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.mikola.podcast.R;
import com.example.mikola.podcast.activitys.PodcastListActivity;
import com.example.mikola.podcast.helpers.PreferenceHelper;
import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.network.DataLoader;
import com.example.mikola.podcast.utils.Constants;

import java.util.List;


public class LoadService extends IntentService {

    public static Intent newIntent(Context c) {
        return new Intent(c, LoadService.class);
    }

    public LoadService() {
        super("LoadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Constants.TAG, "start task");

        if (!isNetworkAvailable())
            return;


        String title = PreferenceHelper.getStoredTitle(this);
        List<Podcast> podcasts = new DataLoader(this).loadPodcasts();
        if (podcasts == null)
            return;
        if (podcasts.size() == 0)
            return;
        Podcast last = podcasts.get(0);
        if (!last.getTitle().equals(title)) {
            PreferenceHelper.setStoredTitle(this, last.getTitle());

            Intent i = new Intent(this, PodcastListActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Resources resources = getResources();
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_podcast))//бігуча строка
                    .setSmallIcon(R.drawable.ic_rss)
                    .setContentTitle(last.getTitle())
                    .setContentText(last.getData())
                    .setLargeIcon(BitmapFactory.decodeResource(resources,
                            R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, notification);

            Log.d(Constants.TAG, "new podcast :" + last.getTitle());
        } else Log.d(Constants.TAG, "no new podcasts");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            Log.d(Constants.TAG,"start");
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HALF_DAY, pi);
        } else {
            Log.d(Constants.TAG,"stop");
            alarmManager.cancel(pi);
            pi.cancel();
        }
        PreferenceHelper.setPrefIsAlarmOn(context,isOn);
    }
}
