package com.example.mikola.podcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends Activity {
    static ArrayList<Podcast> podcasts = new ArrayList<>();
    ListView listOfPodcast;
    AdapterPodcasts adapter;
    Context context;
    static Podcast thisPodcast;
    ImageView actionButton;
    TextView syn;
    static int pos;
    Animation animation;
    boolean notConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        actionButton = (ImageView) findViewById(R.id.syn);
      syn = (TextView) findViewById(R.id.btnsyn);
        syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute();
            }
        });

        context = this;
        listOfPodcast = (ListView) findViewById(R.id.list_podcasts);
        listOfPodcast.setAdapter(adapter);
        listOfPodcast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, PodcastActivity.class);
                thisPodcast = podcasts.get(position);
                pos = position;
                startActivity(intent);

                Log.d("tag", "uidfsjk");
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

        }else {if(MusicService.isRunning())
            for (int i = 0; i <podcasts.size() ; i++) {
                if(i==MusicService.usePosPodcastFromList)
                    podcasts.get(i).setPlaying(true);
                else     podcasts.get(i).setPlaying(false);
            }
            adapter.notifyDataSetChanged();
        }
    }


    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            actionButton.setImageResource(R.drawable.ic_loop_black_24dp);
            actionButton.startAnimation(animation);
            syn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            syn.setText("Cинхронізація . . . ");
        }

        @Override
        protected String doInBackground(String... uri) {
            String dataUrl = "http://feeds.rucast.net/radio-t";
            podcasts.clear();
            URL url;
            HttpURLConnection connection = null;

            try {
// Create connection
                url = new URL(dataUrl);
                connection = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                InputStream is = connection.getInputStream();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(is);

                Element element = doc.getDocumentElement();
                NodeList nodelist = element.getElementsByTagName("item");
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node nNode = nodelist.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        // Set the texts into TextViews from item nodes
                        // Get the title

                        String title = getNode(Constans.TITLE, eElement);
                        String date = getNode("pubDate", eElement);
                        String description = getNode(Constans.DECK, eElement);

                        NodeList music = eElement.getElementsByTagName("media:content");
                        Node mNode = music.item(0).getAttributes().getNamedItem("url");
                        String sound = mNode.getNodeValue();
                        Node lNode = music.item(0).getAttributes().getNamedItem("fileSize");
                        String sound_l = lNode.getNodeValue();


                        org.jsoup.nodes.Document docHtml = Jsoup.parse(description);
                        org.jsoup.nodes.Element link = docHtml.select(Constans.IMAGE).first();
                        String linkHref = link.attr("src"); // "http://example.com/"
                        URL urlimage = new URL(linkHref);
                        Bitmap image = BitmapFactory.decodeStream(urlimage.openConnection().getInputStream());

                        podcasts.add(new Podcast(title, image, date, sound, description, Integer.parseInt(sound_l), linkHref));


                    }
                  /*  ArrayList<Podcast> newPods = new ArrayList<>(podcasts);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
*/
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
            if(MusicService.isRunning())
                for (int i = 0; i <podcasts.size() ; i++) {
                    if(i==MusicService.usePosPodcastFromList)
                    podcasts.get(i).setPlaying(true);
                    else     podcasts.get(i).setPlaying(false);
                }
            if(podcasts.size()==0){
                syn.setText("No Internet");
                syn.setTextColor(getResources().getColor(R.color.colorAccent));
                actionButton.setImageResource(R.drawable.ic_clear_black_24dp);
            }else{

            syn.setText("Синхронізовано");
            actionButton.setImageResource(R.drawable.ic_done_black_24dp);}
            listOfPodcast.setVisibility(View.VISIBLE);
            actionButton.clearAnimation();

            adapter.notifyDataSetChanged();


        }
    }

    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!MusicService.isPlaying()){
            Intent stopIntent = new Intent(context, MusicService.class);
            stopIntent.setAction(Constans.ACTION.STOPFOREGROUND_ACTION);
            stopService(stopIntent);
        }
    }
}
