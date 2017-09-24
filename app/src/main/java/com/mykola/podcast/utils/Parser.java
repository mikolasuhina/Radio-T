package com.mykola.podcast.utils;

import com.mykola.podcast.models.Description;
import com.mykola.podcast.models.Podcast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public final class Parser {

    public static List<Podcast> parseRss(String rss) throws ParserConfigurationException, IOException, SAXException {

        List<Podcast> res = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(rss)));
        Element element = doc.getDocumentElement();


        NodeList nodelist = element.getElementsByTagName("item");
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node nNode = nodelist.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String title = getNode("title", eElement);
                String date = getNode("pubDate", eElement);
                String description = getNode("description", eElement);
                String link = getNode("link", eElement);

                String soundURL = getAttr("media:content", "url", eElement);
                String imageURL = getAttr("itunes:image", "href", eElement);

                Podcast newPodcast = new Podcast(title, imageURL, date, soundURL, description, link);
                res.add(newPodcast);
            }

        }
        return res;
    }

    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }

    private static String getAttr(String sTag, String attr, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag);
        Node nValue = nlList.item(0).getAttributes().getNamedItem(attr);
        return nValue.getNodeValue();
    }

    public static List<Description> parseDescription(String data) {
        List<Description> descriptions = new LinkedList<>();

        org.jsoup.nodes.Document docHtml = Jsoup.parse(data);

        ArrayList<org.jsoup.nodes.Element> d = docHtml.getElementsByTag("ul");
        String itemLink, itemText, itemTime;

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
            else itemTime = null;

            descriptions.add(new Description(itemLink, itemText, itemTime));
        }

        return descriptions;
    }
}

