package com.example.mikola.podcast;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PodcastActivity extends Activity implements View.OnClickListener {
    PodcastItem podcast;
    ImageButton play_pause;
    ImageView image;
    ListView description;
    TextView title;
    Context context;
    SeekBar seekBar;
    TextView info_time_this;
    TextView info_time_all;
    ArrayList<ItemDescriptionList> itemDescriptionList = new ArrayList<>();
    String descriptionText;

    MusicService musicService;

    boolean bound;
    boolean flag;
    Intent intent;
    ImageButton download;
    File myFile;


    void setPlayerParam() {
        if (bound && MainActivity.pos == musicService.usePosPodcastFromList && MusicService.isRunning()) {
            if (musicService.isplaing())
                play_pause.setImageResource(R.drawable.pause);
            else play_pause.setImageResource(R.drawable.play);

            seekBar.setMax(musicService.getLenghtSound());
            seekBar.setProgress(musicService.getCurrentPosition());

            info_time_all.setText(getTimeString(musicService.mediaPlayer.getDuration()));
            info_time_this.setText(getTimeString(musicService.getCurrentPosition()));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);
        podcast = MainActivity.thisPodcast;
        myFile = new File("/sdcard//" + podcast.getTitle() + ".mp3");

        download = (ImageButton) findViewById(R.id.download);

        title = (TextView) findViewById(R.id.podcast_title);

        description = (ListView) findViewById(R.id.podcast_description);
        description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemDescriptionList.get(position).getLinc().contains("http"))
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(itemDescriptionList.get(position).getLinc())));
            }
        });
        intent = new Intent(this, MusicService.class);
        image = (ImageView) findViewById(R.id.podcast_image);
        title.setText(podcast.getTitle());
        context = this;

        image.setImageBitmap(podcast.getImage());
        descriptionText = podcast.getDeck();
        info_time_all = (TextView) findViewById(R.id.time_all__info);
        info_time_this = (TextView) findViewById(R.id.time_this_info);

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(MainActivity.thisPodcast.getSound());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar = (SeekBar) findViewById(R.id.progres);
        info_time_all.setText((getTimeString(mediaPlayer.getDuration())));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    musicService.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        play_pause = (ImageButton) findViewById(R.id.play_pause);
        // setPlayerParam();
        play_pause.setOnClickListener(this);
        final Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        PodcastActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (bound && musicService.isplaing() && MainActivity.pos == musicService.usePosPodcastFromList) {

                    seekBar.setProgress(musicService.getCurrentPosition());
                    info_time_this.setText(getTimeString(musicService.getCurrentPosition()));
                    play_pause.setImageResource(R.drawable.pause);
                }

                if (bound && !musicService.isplaing())
                    if (flag) {
                        play_pause.setImageResource(R.drawable.play);
                        info_time_this.setVisibility(View.INVISIBLE);
                        flag = !flag;
                        mHandler.postDelayed(this, 500);
                    } else {
                        info_time_this.setVisibility(View.VISIBLE);
                        flag = !flag;
                        mHandler.postDelayed(this, 500);
                    }
                else {
                    mHandler.postDelayed(this, 1000);
                    info_time_this.setVisibility(View.VISIBLE);
                }
                ;

            }
        });


        new DescriptionTask().execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setPlayerParam();
    }


    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause: {

                if (bound) {


                    if (musicService.play_pause())
                        play_pause.setImageResource(R.drawable.pause);
                    else play_pause.setImageResource(R.drawable.play);
                    seekBar.setMax(musicService.getLenghtSound());
                    info_time_all.setText(getTimeString(musicService.getLenghtSound()));
                }
                break;

            }
            case R.id.download: {

                File myFile = new File("/sdcard//" + podcast.getTitle() + ".mp3");

                if (myFile.exists()) {
                    Toast.makeText(context, "Даний файл вже завантажено", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("url_download", podcast.getSound());
                    intent.putExtra("name", podcast.getTitle());
                    startService(intent);
                }

            }
        }

    }

    public class DescriptionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            org.jsoup.nodes.Document docHtml = Jsoup.parse(descriptionText);

            ArrayList<org.jsoup.nodes.Element> d = docHtml.getElementsByTag("ul");
            String linkHref = "";
            String linkText = "";
            String emText = "";

            for (org.jsoup.nodes.Element elLI : d.get(0).select("li")) {
                org.jsoup.nodes.Element link = elLI.select("a").first();
                if (link != null) {
                    linkHref = link.attr("href");
                    linkText = link.text();
                } else {
                    linkHref = "";
                    linkText = elLI.text();
                }

                org.jsoup.nodes.Element em = elLI.select("em").first();
                if (em != null)
                    emText = em.text();
                else emText = "";
                itemDescriptionList.add(new ItemDescriptionList(linkHref, linkText, emText));
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AdapterItemDescription adapter = new AdapterItemDescription(itemDescriptionList, context);

            if(myFile.exists()){
               download.setImageResource(R.drawable.music_png);

            }
            // присваиваем адаптер списку
            description.setAdapter(adapter);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            if (MusicService.isPlaying()) {
                musicService.showNotification();
                MusicService.setStartFore(true);
            }
            unbindService(mConnection);
            bound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
            bound = true;
            setPlayerParam();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };


}
