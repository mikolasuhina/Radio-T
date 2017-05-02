package com.example.mikola.podcast.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.mikola.podcast.helpers.PodcastsManager;
import com.example.mikola.podcast.R;
import com.example.mikola.podcast.adapters.AdapterDescriptions;
import com.example.mikola.podcast.models.Description;
import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.services.MusicService;
import com.example.mikola.podcast.network.DownloadSound;
import com.example.mikola.podcast.views.CustomFontTextView;

import org.jsoup.Jsoup;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.mikola.podcast.utils.Constants.ACTION_TYPE;
import static com.example.mikola.podcast.utils.Constants.BROADCAST_ACTION;
import static com.example.mikola.podcast.utils.Constants.SEND_DATA;
import static com.example.mikola.podcast.utils.Constants.SEND_DURATION;
import static com.example.mikola.podcast.utils.Constants.TAG;

/**
 * Created by mykola on 08.02.17.
 */

public class PodcastFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String PODCAST_ID = "com.example.mikola.podcast.POADCAST_ID";

    private ImageButton play_pause;
    private ImageButton download;
    private ImageButton back;
    private ListView description;
    private CustomFontTextView title;
    private SeekBar seekBar;
    private CustomFontTextView info_time_this;
    private CustomFontTextView info_time_all;
    private ArrayList<Description> descriptionList = new ArrayList<>();
    private MusicService musicService;
    private boolean bound;
    private BroadcastReceiver br;

    private Podcast podcast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(PODCAST_ID);
        podcast = PodcastsManager.getInstance(getActivity()).getPodcast(id);
        printLOG("onCreate");
        printLOG("onCreate id = " + id);
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
        printLOG("onCreateView");
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        title = (CustomFontTextView) view.findViewById(R.id.podcast_title);
        description = (ListView) view.findViewById(R.id.podcast_description);
        info_time_all = (CustomFontTextView) view.findViewById(R.id.time_all_info);
        info_time_this = (CustomFontTextView) view.findViewById(R.id.time_this_info);
        play_pause = (ImageButton) view.findViewById(R.id.play_pause_btn);
        seekBar = (SeekBar) view.findViewById(R.id.progres);
        download = (ImageButton) view.findViewById(R.id.download_btn);
        back = (ImageButton) view.findViewById(R.id.back_btn);

        description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (descriptionList.get(position).getLinc() != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(descriptionList.get(position).getLinc())));

            }
        });
        title.setText(podcast.getTitle());

        play_pause.setOnClickListener(this);
        download.setOnClickListener(this);
        back.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);

        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                int type = intent.getIntExtra(ACTION_TYPE, 0);
                printLOG("type = " + type);
                switch (type) {
                    case SEND_DURATION:
                        setDuration();
                        break;
                    case SEND_DATA:
                        update();
                        break;
                }

            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        getContext().registerReceiver(br, intFilt);

        new DescriptionTask().execute(podcast.getDeck());
        return view;
    }


    private void setDuration() {
        seekBar.setMax(musicService.getDuration(podcast));
        info_time_all.setText(getTimeString(musicService.getDuration(podcast)));
    }

    private void update() {
        printLOG("setPlayerParam()");
        if (bound && podcast == MusicService.sPodcast) {
            if (musicService.isPlay(podcast))
                play_pause.setImageResource(R.drawable.icon_pause);
            else play_pause.setImageResource(R.drawable.icon_play);

            seekBar.setProgress(musicService.getCurrentPosition(podcast));

            info_time_this.setText(getTimeString(musicService.getCurrentPosition(podcast)));

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
                if (bound) {
                    musicService.play(podcast);

                    if (musicService.isPlay(podcast))
                        play_pause.setImageResource(R.drawable.icon_pause);
                    else play_pause.setImageResource(R.drawable.icon_play);
                }
                break;

            }
            case R.id.download_btn: {

                File myFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + podcast.getTitle() + ".mp3");

                if (myFile.exists()) {
                    Toast.makeText(getActivity(), R.string.file_exist, Toast.LENGTH_LONG).show();
                } else {
                    new DownloadSound(getActivity(), podcast);
                }
                break;
            }
            case R.id.back_btn: {
                getActivity().finish();
                break;
            }

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            musicService.seekTo(progress, podcast);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public class DescriptionTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            descriptionList.add(new Description(podcast.getImage()));
        }

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
                descriptionList.add(new Description(itemLink, itemText, itemTime));
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AdapterDescriptions adapter = new AdapterDescriptions(descriptionList, getActivity());
            description.setAdapter(adapter);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        printLOG("onStart()");
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStop() {
        super.onStop();
        printLOG("onStop()");
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
            printLOG("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            printLOG("onServiceDisconnected");
        }
    };

    private void printLOG(String text) {
        Log.d(TAG,text);
    }
}
