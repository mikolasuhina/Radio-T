package com.example.mikola.podcast.network;

import android.content.Context;

import com.example.mikola.podcast.models.Podcast;
import com.example.mikola.podcast.utils.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.example.mikola.podcast.utils.Constants.DECK;
import static com.example.mikola.podcast.utils.Constants.HREF;
import static com.example.mikola.podcast.utils.Constants.ITEM;
import static com.example.mikola.podcast.utils.Constants.ITUNES_IMAGE;
import static com.example.mikola.podcast.utils.Constants.MEDIA_CONTENT;
import static com.example.mikola.podcast.utils.Constants.PUB_DATA;
import static com.example.mikola.podcast.utils.Constants.dataUrl;

/**
 * Created by mykola on 08.04.17.
 */

public class DataLoader {
    private Context context;

    public DataLoader(Context context) {
        this.context = context;
    }

    public ArrayList<Podcast> loadPodcasts() {
        URL url;
        HttpURLConnection connection = null;

        try {
//               Create connection
            url = new URL(dataUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();


            ArrayList<Podcast> result = parseStream(is);
            is.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private ArrayList<Podcast> parseStream(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Podcast> res = new ArrayList<>();


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element element = doc.getDocumentElement();


        NodeList nodelist = element.getElementsByTagName(ITEM);
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node nNode = nodelist.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String title = getNode(Constants.TITLE, eElement);
                String date = getNode(PUB_DATA, eElement);
                String description = getNode(DECK, eElement);

                String soundURL = getAttr(MEDIA_CONTENT, Constants.URL, eElement);

                String imageURL = getAttr(ITUNES_IMAGE, HREF, eElement);

                Podcast newPodcast = new Podcast(title, imageURL, date, soundURL, description);
                res.add(newPodcast);
            }


        }
        return res;
    }

    private String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }

    private String getAttr(String sTag, String attr, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag);
        Node nValue = nlList.item(0).getAttributes().getNamedItem(attr);
        return nValue.getNodeValue();
    }
}
