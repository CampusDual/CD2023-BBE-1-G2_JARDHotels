package com.campusdual.jardhotelsontimize.model.core.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TouristicPlace {
    private double longitude;
    private double latitude;
    private String type;
    private String name;

    public TouristicPlace(double longitude, double latitude, String type, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TouristicPlace{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<TouristicPlace> parseTouristicPlaces(String json) {
        List<TouristicPlace> touristicPlaces = new ArrayList<>();

        json = extractTextBetweenBrackets(json);

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latitude = jsonObject.getDouble("lat");
                double longitude = jsonObject.getDouble("lon");

                String type = jsonObject.getJSONObject("tags").getString("tourism");
                String name = jsonObject.getJSONObject("tags").getString("name");

                try{
                    String artWorkType = jsonObject.getJSONObject("tags").getString("artwork_type");
                    type = artWorkType;
                }catch (Exception e){
                }

                TouristicPlace touristicPlace = new TouristicPlace(longitude, latitude, type, name);
                touristicPlaces.add(touristicPlace);
            }catch (Exception e){
                System.err.println("Imposible to parse " + jsonArray.getJSONObject(i));
            }
        }


        return touristicPlaces;
    }

    private static String extractTextBetweenBrackets(String input) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return "["+matcher.group(1)+"]";
        } else {
            return input;
        }
    }
}
