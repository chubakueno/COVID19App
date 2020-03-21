package com.example.covid19.utils;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Anonimizer {
    private static final double randRadiusMeters = 25.0;
    private static final double earthRadius=6378137;

    private static long parseDate(String date) throws ParseException {
        date = date.replace('T',' ');
        date = date.replace('Z',' ');
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date parsed = sdf.parse(date);
        return parsed == null ? 0 : parsed.getTime();
    }
    public static String anonimize(String raw){
        Gson gson = new Gson();
        DateDataInternal[] kmlArray = gson.fromJson(raw, DateDataInternal[].class);
        List<DateData> anonimized = new ArrayList<>();
        for(DateDataInternal dateDataInternal: kmlArray){
            String kmlString = dateDataInternal.data;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder ;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(kmlString)));
                NodeList placemarks = doc.getElementsByTagName("Placemark");
                for (int i=0;i<placemarks.getLength();++i){
                    Element placemark = (Element)placemarks.item(i);
                    Element timeSpan = (Element)placemark.getElementsByTagName("TimeSpan").item(0);
                    long start = parseDate(timeSpan.getElementsByTagName("begin").item(0).getTextContent());
                    long end = parseDate(timeSpan.getElementsByTagName("end").item(0).getTextContent());
                    String datatype = "unknown";
                    if (placemark.getElementsByTagName("Point").getLength() > 0)
                        datatype = "point";
                    else if (placemark.getElementsByTagName("LineString").getLength() > 0)
                        datatype = "path";
                    NodeList coordinates = placemark.getElementsByTagName("coordinates");
                    List<LatLng> latLngList = new ArrayList<>();
                    for (int j=0;j<coordinates.getLength();++j) {
                        Node coordinate = coordinates.item(j);
                        String[] coordlist = coordinate.getTextContent().split("\\s+");
                        for (String s : coordlist) {
                            String[] lnglat = s.split(",");
                            Random random = new Random();
                            double lng = Double.valueOf(lnglat[0]);
                            double lat = Double.valueOf(lnglat[1]);
                            double dLat = (randRadiusMeters*random.nextDouble()*2-1)/earthRadius;
                            double dLon = (randRadiusMeters*random.nextDouble()*2-1)/(earthRadius*Math.cos(Math.PI*lat/180));
                            latLngList.add(new LatLng(lat+dLat*180/Math.PI, lng+dLon*180/Math.PI));
                        }
                    }
                    DateData dateData = new DateData(dateDataInternal.date,start,end,datatype,latLngList);
                    anonimized.add(dateData);
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return gson.toJson(anonimized);
    }
}
