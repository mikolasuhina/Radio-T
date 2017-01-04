package com.example.mikola.podcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends Service {
    public static final String URL_FOR_DOWNLOAD = "url_download";
    public static final String FILE_NAME_FOR_DOWNLOAD = "name";

    private String title;
    private String urdDownloading;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Integer notificationID = 100;
    private int incr = 0;
    private int lenghtOfFile = 0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        urdDownloading = intent.getStringExtra(URL_FOR_DOWNLOAD);
        title = intent.getStringExtra(FILE_NAME_FOR_DOWNLOAD);
        new DownloadFile().execute();
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title)
                .setContentText(getString(R.string.downloading))
                .setSmallIcon(android.R.drawable.stat_sys_download);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {


                        while (incr < 100) {

                            mBuilder.setProgress(100, incr, false);

                            mNotifyManager.notify(notificationID, mBuilder.build());

                            try {

                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                                ;
                            }
                        }

                        mBuilder.setContentText(getString(R.string.download_complete))

                                .setProgress(0, 0, false);
                        mNotifyManager.notify(notificationID, mBuilder.build());
                    }
                }

        ).start();

        return START_NOT_STICKY;
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            try {
                URL url = new URL(urdDownloading);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                lenghtOfFile = conexion.getContentLength();


                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard//" + title + ".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            stopSelf();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            incr = values[0];
            super.onProgressUpdate(values);
        }
    }


}