package com.example.mikola.podcast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikola.podcast.adapters.AdapterPodcasts;
import com.example.mikola.podcast.objs.Podcast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.example.mikola.podcast.Constants.ITEM;
import static com.example.mikola.podcast.Constants.MEDIA_CONTENT;
import static com.example.mikola.podcast.Constants.PUB_DATA;
import static com.example.mikola.podcast.Constants.SRC;
import static com.example.mikola.podcast.Constants.dataUrl;

/**
 * Created by mykola on 08.02.17.
 */

public class PodcastListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ListView listOfPodcast;
    private SwipeRefreshLayout refreshLayout;
    private ImageView statusImg;
    private TextView statusText;
    private AdapterPodcasts adapter;

    private Animation animation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_podcast, container, false);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);

        statusImg = (ImageView) view.findViewById(R.id.status_img);
        statusText = (TextView) view.findViewById(R.id.status_text);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listOfPodcast = (ListView) view.findViewById(R.id.list_podcasts);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.BLUE);
        statusText.setOnClickListener(this);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        new RequestTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.status_text: {
                new RequestTask().execute();
                break;
            }
        }
    }

    private class RequestTask extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusImg.setImageResource(R.drawable.icon_loop);
            statusImg.startAnimation(animation);
            statusText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            statusText.setText(R.string.synchronize);
        }

        @Override
        protected Integer doInBackground(String... uri) {

            URL url;
            HttpURLConnection connection = null;

            try {
//               Create connection
                url = new URL(dataUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(60 * 1000);

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
                        String title = getNode(Constants.TITLE, eElement);

                        if (DataPodcasts.getInstance(getActivity()).getPodcast(title) == null) {
                            String date = getNode(PUB_DATA, eElement);
                            String description = getNode(Constants.DECK, eElement);

                            NodeList music = eElement.getElementsByTagName(MEDIA_CONTENT);
                            Node mNode = music.item(0).getAttributes().getNamedItem(Constants.URL);
                            String sound = mNode.getNodeValue();

                            org.jsoup.nodes.Document docHtml = Jsoup.parse(description);
                            org.jsoup.nodes.Element link = docHtml.select(Constants.IMAGE).first();
                            String linkHref = link.attr(SRC);
                            URL urlimage = new URL(linkHref);
                            Bitmap image = BitmapFactory.decodeStream(urlimage.openConnection().getInputStream());

                            Podcast newPodcast = new Podcast(title, image, date, sound, description);
                            DataPodcasts.getInstance(getActivity()).addPodcast(newPodcast);
                        }

                    }
                }

                is.close();
                return HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return HttpURLConnection.HTTP_INTERNAL_ERROR;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                statusText.setText(R.string.error_text);
                statusText.setTextColor(getResources().getColor(R.color.colorAccent));
                statusImg.setImageResource(R.drawable.icon_error);
            } else {
                statusText.setText(R.string.synchronize_complete);
                statusImg.setImageResource(R.drawable.icon_done);
            }

            refreshLayout.setRefreshing(false);
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

    private void updateUI() {
        DataPodcasts data = DataPodcasts.getInstance(getActivity());
        List<Podcast> podcasts = data.getPodcasts();

        if (adapter == null) {
            adapter = new AdapterPodcasts(podcasts, getActivity());
            listOfPodcast.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if (!MusicService.isRunning()) {
            new RequestTask().execute();
        } else {
            listOfPodcast.setAdapter(adapter);
            listOfPodcast.setVisibility(View.VISIBLE);
            statusImg.clearAnimation();
            statusText.setText(R.string.synchronize_complete);
            statusImg.setImageResource(R.drawable.icon_done);

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!MusicService.isRunning()) {
            Intent startIntent = new Intent(getActivity(), MusicService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            getActivity().startService(startIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!MusicService.isStarted()) {
            Intent stopIntent = new Intent(getActivity(), MusicService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            getActivity().stopService(stopIntent);
        }
    }


}
