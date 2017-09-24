package com.mykola.podcast.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mykola.podcast.R;
import com.mykola.podcast.adapters.AdapterDescription;
import com.mykola.podcast.helpers.NetworkHelper;
import com.mykola.podcast.helpers.PodcastsManager;
import com.mykola.podcast.models.Description;
import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.services.MusicService;
import com.mykola.podcast.utils.Parser;
import com.mykola.podcast.utils.TimeUtils;
import com.mykola.podcast.views.CustomFontTextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static com.mykola.podcast.services.MusicService.PLAY_ACTION;


public class PodcastFragment extends SingleFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String PODCAST_TITLE = "com.mykola.podcast.PODCAST_TITLE";

    private final String TAG = getClass().getSimpleName();

    private ImageView appBarImage;
    private Toolbar mToolbar;
    private FloatingActionButton mStartPlayButton;

    private RecyclerView links;

    private View playerView;
    private ImageButton play_pause;
    private SeekBar seekBar;
    private TextView playerCurrentTime;
    private TextView playerDuration;

    private Podcast podcast;

    public static PodcastFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(PODCAST_TITLE, title);
        PodcastFragment fragment = new PodcastFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        String podcastTitle = getArguments().getString(PODCAST_TITLE);
        podcast = PodcastsManager.getInstance().getPodcast(podcastTitle);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_podcast, container, false);

        initViews(view);
        setViewsParameters();

        return view;
    }


    private void initViews(View view) {
        mStartPlayButton = (FloatingActionButton) view.findViewById(R.id.start_play_button);
        links = (RecyclerView) view.findViewById(R.id.podcast_description);
        playerView = view.findViewById(R.id.player_layout);
        playerDuration = (CustomFontTextView) view.findViewById(R.id.duration_player);
        playerCurrentTime = (CustomFontTextView) view.findViewById(R.id.current_time_player);
        play_pause = (ImageButton) view.findViewById(R.id.play_pause_btn);
        seekBar = (SeekBar) view.findViewById(R.id.progress);
        appBarImage = (ImageView) view.findViewById(R.id.app_bar_image);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
    }

    private void setViewsParameters() {
        Picasso.with(getContext()).load(podcast.getImage()).into(appBarImage);

        mToolbar.setTitle(podcast.getTitle());
        mToolbar.setSubtitle(podcast.getDate());

        List<Description> linksList = Parser.parseDescription(podcast.getDescription());
        links.setAdapter(new AdapterDescription(linksList, getContext()));
        links.setLayoutManager(new LinearLayoutManager(getContext()));

        seekBar.setOnSeekBarChangeListener(this);
        play_pause.setOnClickListener(this);
        mStartPlayButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause_btn: {
                musicService.play();
                break;
            }

            case R.id.start_play_button: {

                musicService.init(podcast);

                Intent playIntent = new Intent(getContext(), MusicService.class);
                playIntent.setAction(PLAY_ACTION);
                getActivity().startService(playIntent);

                break;
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.podcast_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.download_item:

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File myFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + podcast.getTitle() + ".mp3");

                    if (myFile.exists()) {
                        Toast.makeText(getActivity(), R.string.file_exist, Toast.LENGTH_LONG).show();
                    } else {
                        NetworkHelper.downloadSound(getActivity(), podcast);
                    }
                } else {
                    Toast.makeText(musicService, R.string.not_available, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.open_in_browser_item:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(podcast.getLink()));
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            if (musicService.isPlayingPodcast(podcast))
                musicService.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    protected void updatePlayerView() {
        if (bound && musicService.isPlayingPodcast(podcast)) {
            if (musicService.isPlay())
                play_pause.setImageResource(R.drawable.icon_pause);
            else play_pause.setImageResource(R.drawable.icon_play);

            seekBar.setProgress(musicService.getCurrentPosition());
            playerCurrentTime.setText(TimeUtils.getTimeString(musicService.getCurrentPosition()));
        }
    }

    @Override
    protected void configurePlayerView() {
        if (bound && musicService.isPlayingPodcast(podcast)) {
            playerView.setVisibility(View.VISIBLE);
            mStartPlayButton.setVisibility(View.GONE);
            seekBar.setMax(musicService.getDuration());
            playerDuration.setText(TimeUtils.getTimeString(musicService.getDuration()));
        } else {
            playerView.setVisibility(View.GONE);
            mStartPlayButton.setVisibility(View.VISIBLE);
        }
    }
}
