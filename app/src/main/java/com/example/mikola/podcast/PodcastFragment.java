package com.example.mikola.podcast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by mykola on 08.02.17.
 */

public class PodcastFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String PODCAST_ID = "com.example.mikola.podcast.POADCAST_ID";

    private ImageButton play_pause;
    private ImageView image;
    private ListView description;
    private TextView title;
    private SeekBar seekBar;
    private TextView info_time_this;
    private TextView info_time_all;
    private ArrayList<ItemDescriptionList> itemDescriptionList = new ArrayList<>();
    private MusicService musicService;
    boolean bound;
    boolean flag;

    private Podcast podcast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(PODCAST_ID);
        podcast = PodcastsData.getInstance(getActivity()).getPodcast(id);
    }

    public static PodcastFragment newInstance(UUID id) {

        Bundle args = new Bundle();
        args.putSerializable(PODCAST_ID, id);
        PodcastFragment fragment = new PodcastFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        title = (TextView) view.findViewById(R.id.podcast_title);
        description = (ListView) view.findViewById(R.id.podcast_description);
        image = (ImageView) view.findViewById(R.id.podcast_image);
        info_time_all = (TextView) view.findViewById(R.id.time_all_info);
        info_time_this = (TextView) view.findViewById(R.id.time_this_info);
        play_pause = (ImageButton) view.findViewById(R.id.play_pause_btn);
        seekBar = (SeekBar) view.findViewById(R.id.progres);



        description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemDescriptionList.get(position).getLinc() != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(itemDescriptionList.get(position).getLinc())));

            }
        });
        title.setText(podcast.getTitle());
        image.setImageBitmap(podcast.getImage());

        play_pause.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);


        final Handler mHandler = new Handler();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (musicService.isPlaying()) {
                   setPlayerParam();
                }

                if (bound && !musicService.isPlaying())
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

            }
        });


        new DescriptionTask().execute(podcast.getDeck());
        return view;
    }


    private void setPlayerParam() {
        if (bound && podcast.getId().equals(MusicService.getId())) {
            if (musicService.isPlaying())
                play_pause.setImageResource(R.drawable.pause);
            else play_pause.setImageResource(R.drawable.play);

            seekBar.setMax(musicService.getLenghtSound());
            seekBar.setProgress(musicService.getCurrentPosition());

            info_time_all.setText(getTimeString(musicService.getLenghtSound()));
            info_time_this.setText(getTimeString(musicService.getCurrentPosition()));

        }
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
            case R.id.play_pause_btn: {
                Log.d("TAG", "pressed play");
                if (bound) {
                    musicService.play(podcast);
                    if (musicService.isPlaying())
                        play_pause.setImageResource(R.drawable.pause);
                    else play_pause.setImageResource(R.drawable.play);
                }
                break;

            }
            case R.id.download: {

                File myFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + podcast.getTitle() + ".mp3");

                if (myFile.exists()) {
                    Toast.makeText(getActivity(), R.string.file_exist, Toast.LENGTH_LONG).show();
                } else {
                    new DownloadSound(getActivity(), podcast);
                }
                break;
            }

        }

    }

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

    public class DescriptionTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            org.jsoup.nodes.Document docHtml = Jsoup.parse(strings[0]);

            ArrayList<org.jsoup.nodes.Element> d = docHtml.getElementsByTag("ul");
            String itemLink = "";
            String itemText = "";
            String itemTime = "";

            for (org.jsoup.nodes.Element elLI : d.get(0).select("li")) {
                org.jsoup.nodes.Element link = elLI.select("a").first();
                if (link != null) {
                    itemLink = link.attr("href");
                    itemText = link.text();
                } else {
                    itemLink = null;
                    itemText = elLI.text();
                }

                org.jsoup.nodes.Element em = elLI.select("em").first();
                if (em != null)
                    itemTime = em.text();
                else itemTime = "";
                itemDescriptionList.add(new ItemDescriptionList(itemLink, itemText, itemTime));
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AdapterItemDescription adapter = new AdapterItemDescription(itemDescriptionList, getActivity());
            description.setAdapter(adapter);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (MusicService.isRunning()) {
            Intent intent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            getActivity().unbindService(mConnection);
            bound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
}
