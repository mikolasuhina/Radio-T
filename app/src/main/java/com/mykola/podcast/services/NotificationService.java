package com.mykola.podcast.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.mykola.podcast.R;
import com.mykola.podcast.activitys.PodcastActivity;
import com.mykola.podcast.helpers.NetworkHelper;
import com.mykola.podcast.helpers.PreferenceHelper;
import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.network.retrofit.APICallbacks;

import java.util.List;

import static com.mykola.podcast.fragments.PodcastFragment.PODCAST_TITLE;


public class NotificationService extends IntentService {

    private static final int NOTIFICATION_ID = 2;

    public final String TAG = getClass().getSimpleName();

    public NotificationService() {
        super("NotificationServiceThread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "start task");

        final String title = PreferenceHelper.getStoredTitle(this);

        NetworkHelper.loadPodcasts(new APICallbacks<List<Podcast>>() {
            @Override
            public void onResponse(List<Podcast> podcasts) {

                if (podcasts.isEmpty())
                    return;

                Podcast lastPodcast = podcasts.get(0);

                if (!lastPodcast.getTitle().equals(title)) {
                    PreferenceHelper.setStoredTitle(getBaseContext(), lastPodcast.getTitle());

                    showNotification(lastPodcast);

                    Log.d(TAG, "new podcast :" + lastPodcast.getTitle());
                } else {
                    Log.d(TAG, "no new podcasts");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "error: " + t.getMessage());
            }
        });

    }

    private void showNotification(Podcast last) {

        Intent i = new Intent(getBaseContext(), PodcastActivity.class);
        i.putExtra(PODCAST_TITLE, last.getTitle());

        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, i, 0);

        Notification notification = new NotificationCompat.Builder(getBaseContext())
                .setTicker(getResources().getString(R.string.new_podcast))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(last.getTitle())
                .setContentText(last.getDate())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getBaseContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }


    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HALF_DAY, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
        PreferenceHelper.setPrefIsAlarmOn(context, isOn);
    }
}
