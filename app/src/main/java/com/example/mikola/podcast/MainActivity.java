package com.example.mikola.podcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.example.mikola.podcast.Constans.FILE_SIZE;
import static com.example.mikola.podcast.Constans.ITEM;
import static com.example.mikola.podcast.Constans.MEDIA_CONTENT;
import static com.example.mikola.podcast.Constans.PUB_DATA;
import static com.example.mikola.podcast.Constans.SRC;
import static com.example.mikola.podcast.Constans.TITLE;
import static com.example.mikola.podcast.Constans.dataUrl;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener , View.OnClickListener {
    public static ArrayList<PodcastItem> podcasts = new ArrayList<>();
    public static PodcastItem thisPodcast;
    public static int pos;

    private ListView listOfPodcast;
    private SwipeRefreshLayout refreshLayout;
    private ImageView statusImg;
    private TextView statusText;

    private AdapterPodcasts adapter;
    private Context context;

    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        context = this;

        statusImg = (ImageView) findViewById(R.id.status_img);
        statusText = (TextView) findViewById(R.id.status_text);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        listOfPodcast = (ListView) findViewById(R.id.list_podcasts);

        refreshLayout.setOnRefreshListener(this);
        statusText.setOnClickListener(this);


        listOfPodcast.setAdapter(adapter);
        listOfPodcast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                thisPodcast = podcasts.get(position);
                pos = position;

                Intent intent = new Intent(context, PodcastActivity.class);
                startActivity(intent);

                Log.d("tag", "start PodcastActivity");
            }
        });
        adapter = new AdapterPodcasts(podcasts, context);

        listOfPodcast.setAdapter(adapter);
        new RequestTask().execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!MusicService.isRunning()) {
            Intent startIntent = new Intent(context, MusicService.class);
            startIntent.setAction(Constans.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);

        } else {
            if (MusicService.isRunning())
                for (int i = 0; i < podcasts.size(); i++) {
                    if (i == MusicService.usePosPodcastFromList)
                        podcasts.get(i).setPlaying(true);
                    else podcasts.get(i).setPlaying(false);
                }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        new RequestTask().execute();
        refreshLayout.setRefreshing(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.status_text:{
                new RequestTask().execute();
                break;
            }
        }
    }


    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusImg.setImageResource(R.drawable.ic_loop_black_24dp);
            statusImg.startAnimation(animation);
            statusText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            statusText.setText(R.string.synchronize);

            podcasts.clear();
        }

        @Override
        protected String doInBackground(String... uri) {

            URL url;
            HttpURLConnection connection = null;

            try {
//               Create connection
                url = new URL(dataUrl);
                connection = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                InputStream is = connection.getInputStream();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(is);

                Element element = doc.getDocumentElement();
                NodeList nodelist = element.getElementsByTagName(ITEM);
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node nNode = nodelist.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        String title = getNode(Constans.TITLE, eElement);
                        String date = getNode(PUB_DATA, eElement);
                        String description = getNode(Constans.DECK, eElement);

                        NodeList music = eElement.getElementsByTagName(MEDIA_CONTENT);
                        Node mNode = music.item(0).getAttributes().getNamedItem(Constans.URL);
                        String sound = mNode.getNodeValue();
                        Node lNode = music.item(0).getAttributes().getNamedItem(FILE_SIZE);
                        String sound_l = lNode.getNodeValue();

                        org.jsoup.nodes.Document docHtml = Jsoup.parse(description);
                        org.jsoup.nodes.Element link = docHtml.select(Constans.IMAGE).first();
                        String linkHref = link.attr(SRC);
                        URL urlimage = new URL(linkHref);
                        Bitmap image = BitmapFactory.decodeStream(urlimage.openConnection().getInputStream());

                        podcasts.add(new PodcastItem(title, image, date, sound, description,
                                Integer.parseInt(sound_l), linkHref));
                    }
                }

                is.close();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (MusicService.isRunning())
                for (int i = 0; i < podcasts.size(); i++) {
                    if (i == MusicService.usePosPodcastFromList)
                        podcasts.get(i).setPlaying(true);
                    else podcasts.get(i).setPlaying(false);
                }
            if (podcasts.size() == 0) {
                statusText.setText(R.string.error_text);
                statusText.setTextColor(getResources().getColor(R.color.colorAccent));
                statusImg.setImageResource(R.drawable.ic_clear_black_24dp);
            } else {
                statusText.setText(R.string.synchronize_complete);
                statusImg.setImageResource(R.drawable.ic_done_black_24dp);
            }


            listOfPodcast.setVisibility(View.VISIBLE);
            statusImg.clearAnimation();

            adapter.notifyDataSetChanged();


        }
    }

    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!MusicService.isPlaying()) {
            Intent stopIntent = new Intent(context, MusicService.class);
            stopIntent.setAction(Constans.ACTION.STOPFOREGROUND_ACTION);
            stopService(stopIntent);
        }
    }


}
