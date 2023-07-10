package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.api.core.service.ICountryService;
import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.campusdual.jardhotelsontimize.model.core.dao.BookingDao;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.ontimize.jee.common.db.SQLStatementBuilder.*;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.rest.ORestController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/hotels")
public class HotelRestController extends ORestController<IHotelService> {

    @Autowired
    private IHotelService iHotelService;

    @Autowired
    private ICountryService iCountryService;

    @Autowired
    private IBookingService iBookingService;

    @Autowired
    private IRoomService iRoomService;

    @Override
    public IHotelService getService() {
        return this.iHotelService;
    }

    /**Metodo de puerta de hotel**/

    @RequestMapping(value = "/hotelDoor", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult hotelDoor(@RequestBody Map<String, Object> req){
        Map<String, Object> filter = (Map<String, Object>) req.get("filter");

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        if(filter.get("code") == null){
            error.setMessage("Missing code");
            return error;
        }

        if(filter.get("hotel") == null){
            error.setMessage("Missing hotel");
            return error;
        }

        try {
            Map<String, Object>key = new HashMap<>();
            key.put("id", filter.get("hotel"));

            List<String>attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult hotelQuery = iHotelService.hotelQuery(key, attrList);
            if(hotelQuery.getCode() == EntityResult.OPERATION_WRONG){
                error.setMessage(hotelQuery.getMessage());
                return error;
            }

            key = new HashMap<>();
            key.put("code", filter.get("code"));

            attrList.add("room");
            attrList.add("arrivaldate");
            attrList.add("departuredate");

            EntityResult bookingQuery = iBookingService.bookingQuery(key, attrList);

            if(bookingQuery.getCode() == EntityResult.OPERATION_WRONG){
                error.setMessage(bookingQuery.getMessage());
                return error;
            }

            List<Integer>rooms = (List<Integer>) bookingQuery.get("room");
            key = new HashMap<>();
            key.put("id", rooms.get(0));

            attrList = new ArrayList<>();
            attrList.add("id");
            attrList.add("hotel");

            EntityResult roomQuery = iRoomService.roomQuery(key, attrList);
            List<Integer>hotels = (List<Integer>) roomQuery.get("hotel");

            int hotelToPass = (int) filter.get("hotel");
            int hotelFromBooking = hotels.get(0);

            if(hotelToPass == hotelFromBooking){
                List<Date>arrivalDates = (List<Date>) bookingQuery.get("arrivaldate");
                List<Date>departureDates = (List<Date>) bookingQuery.get("departuredate");

                Date fechaActual = new Date();
                if (fechaActual.compareTo(arrivalDates.get(0)) >= 0 && fechaActual.compareTo(departureDates.get(0)) <= 0) {
                    EntityResult ok = new EntityResultMapImpl();
                    ok.setMessage("Access granted");
                    return ok;
                } else {
                    error.setMessage("Access denied");
                    return error;
                }
            }else{
                error.setMessage("Access denied");
                return error;
            }

        }catch (Exception e){
            e.printStackTrace();
            error.setMessage(e.getMessage());
            return error;
        }

    }

    /**Metodo de filtro**/

    @RequestMapping(value = "/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult filter(@RequestBody Map<String, Object> req) {
        try {
            boolean deleteLatitude = false;
            boolean deleteLongitude = false;

            List<String> columns = (List<String>) req.get("columns");
            Map<String, Object> filter = (Map<String, Object>) req.get("filter");
            Map<String, Object> key = new HashMap<>();

            key.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                    concatenateExpressions(filter));

            if(!columns.contains("latitude")){
                columns.add("latitude");
                deleteLatitude = true;
            }
            if(!columns.contains("longitude")){
                columns.add("longitude");
                deleteLongitude = true;
            }

            EntityResult hotelQuery = iHotelService.hotelQuery(key, columns);

            if(filter.get("latitude") != null && filter.get("longitude") != null){
                hotelQuery = calculateDistance(hotelQuery, (double)filter.get("latitude"), (double)filter.get("longitude"));
                hotelQuery = orderQueryByDistance(hotelQuery, columns);
            }else if (filter.get("latitude") != null && filter.get("longitude") == null){
                throw new RuntimeException("You cannot apply the distance filter without the longitude. Please add it or remove latitude");
            }else if (filter.get("latitude") == null && filter.get("longitude") != null){
                throw new RuntimeException("You cannot apply the distance filter without the latitude. Please add it or remove longitude");
            }
            boolean isEmpty=false;
            if (hotelQuery.get("latitude")==null){
                isEmpty=true;
            }
            if(deleteLatitude){
                hotelQuery.remove("latitude");
            }
            if(deleteLongitude){
                hotelQuery.remove("longitude");
            }
            if (isEmpty){
                EntityResult empty = new EntityResultMapImpl();
                empty.setCode(EntityResult.OPERATION_WRONG);
                empty.setMessage("No hotels founded with this filter");
                return empty;
            }

            return hotelQuery;
        } catch (Exception e) {
            e.printStackTrace();
            EntityResult res = new EntityResultMapImpl();
            res.setCode(EntityResult.OPERATION_WRONG);
            res.setMessage(e.getMessage());

            return res;
        }
    }

    /**Métodos de cálculo de distancias**/
    private EntityResult calculateDistance(EntityResult query, double latitude, double longitude) {
        List<BigDecimal>latitudeList = (List<BigDecimal>) query.get("latitude");
        List<BigDecimal>longitudeList = (List<BigDecimal>) query.get("longitude");
        if (latitudeList == null) {
            latitudeList = new ArrayList<>();
        }
        if (longitudeList == null) {
            longitudeList = new ArrayList<>();
        }
        List<Double> distances = calculateDistance(latitudeList, longitudeList, latitude, longitude);
        query.put("distance(km)", distances);
        return query;
    }

    public static List<Double> calculateDistance(List<BigDecimal> latitudeList, List<BigDecimal> longitudeList, double latitude, double longitude) {
        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < latitudeList.size(); i++) {
            double latHotel = Double.parseDouble(latitudeList.get(i).toString());
            double lonHotel = Double.parseDouble(longitudeList.get(i).toString());
            double distance = calculateDistance(latHotel, lonHotel, latitude, longitude);
            distances.add(distance);
        }

        return distances;
    }

    public static double calculateDistance(double latHotel, double lonHotel, double latitude, double longitude) {
        double earthRadius = 6371; //Radio de la Tierra en kilómetros

        double lat1Rad = Math.toRadians(latHotel);
        double lon1Rad = Math.toRadians(lonHotel);
        double lat2Rad = Math.toRadians(latitude);
        double lon2Rad = Math.toRadians(longitude);

        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;

        return distance;
    }

    private EntityResult orderQueryByDistance(EntityResult hotelQuery, List<String> columns) {
        List<Double> distances = (List<Double>) hotelQuery.get("distance(km)");
        List<List<Object>> listAttrlist = new ArrayList<>();

        for(String column : columns){
            if(!column.equals("distance(km)")){
                List<Object>listAttr = (List<Object>) hotelQuery.get(column);
                listAttrlist.add(listAttr);
            }
        }

        for (int i = 0; i < distances.size() - 1; i++) {
            for (int j = 0; j < distances.size() - i - 1; j++) {
                Double now = distances.get(j);
                Double next = distances.get(j + 1);

                if (now.compareTo(next) > 0) {
                    for(List<Object>list : listAttrlist){
                        Object oNow = new ArrayList<>(list).get(j);
                        Object oNext = new ArrayList<>(list).get(j + 1);
                        list.set(j, oNext);
                        list.set(j + 1, oNow);
                    }
                    distances.set(j, next);
                    distances.set(j + 1, now);
                }
            }
        }

        EntityResult toret = new EntityResultMapImpl();
        toret.put("distance(km)", distances);
        for(int i = 0; i < columns.size(); i++){
            if(!columns.get(i).equals("distance(km)")){
                toret.put(columns.get(i), listAttrlist.get(i));
            }
        }

        return toret;
    }

    /**Método de concatenación y filtros**/
    private BasicExpression concatenateExpressions(Map<String, Object> filter) {

        // filtro estrellas
        int stars_min = 1, stars_max = 5;

        try {
            if (filter.get("stars_min") != null) {
                stars_min = (int) filter.get("stars_min");
            }
            if (filter.get("stars_max") != null) {
                stars_max = (int) filter.get("stars_max");
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Stars must be a whole number");
        }

        if (stars_min < 1 || stars_min > 5 || stars_max < 1 || stars_max > 5) {
            throw new RuntimeException("Stars must be between 1 and 5");
        } else if (stars_min > stars_max) {
            throw new RuntimeException("Min stars must be lower or equal than max");
        }

        BasicExpression bexp = searchBetweenStars(stars_min, stars_max);

        // filtro nombre
        if (filter.get("name") != null) {
            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchLikeName((String) filter.get("name")));
        }

        // filtro direccion
        if (filter.get("address") != null) {
            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchLikeAddress((String) filter.get("address")));
        }

        // filtro pais
        if (filter.get("country") != null) {

            try {
                int country = (int) filter.get("country");
            } catch (Exception e) {
                throw new RuntimeException("Country must be a whole number");
            }

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", filter.get("country"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult entityResult = iCountryService.countryQuery(keyMap, attrList);

            if (entityResult.getMessage().contains("The country doesn't exist")) {
                throw new RuntimeException("Country must exist");
            }

            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchByCountry((int) filter.get("country")));
        }

        //filtro latitud
        if(filter.get("latitude") != null){
            try{
                double latitude = (double)filter.get("latitude");
                if(latitude < -90 || latitude > 90){
                    throw new RuntimeException("Latitude must be a range between -90 and +90");
                }
            }catch (Exception e){
                throw new RuntimeException("Latitude must be a decimal number");
            }
        }

        //filtro longitud
        if(filter.get("longitude") != null){
            try{
                double longitude = (double)filter.get("longitude");
                if(longitude < -180 || longitude > 180){
                    throw new RuntimeException("Longitude must be a range between -180 and +180");
                }
            }catch (Exception e){
                throw new RuntimeException("Longitude must be a decimal number");
            }
        }

        return bexp;
    }

    private BasicExpression searchBetweenStars(int stars_min, int stars_max) {

        BasicField field = new BasicField(HotelDao.ATTR_STARS);
        BasicExpression bexp1 = new BasicExpression(field, BasicOperator.MORE_EQUAL_OP, stars_min);
        BasicExpression bexp2 = new BasicExpression(field, BasicOperator.LESS_EQUAL_OP, stars_max);
        return new BasicExpression(bexp1, BasicOperator.AND_OP, bexp2);
    }

    private BasicExpression searchLikeName(String name) {

        BasicField field = new BasicField(HotelDao.ATTR_NAME);
        return new BasicExpression(field, BasicOperator.LIKE_OP, "%" + name + "%");
    }

    private BasicExpression searchLikeAddress(String address) {

        BasicField field = new BasicField(HotelDao.ATTR_ADDRESS);
        return new BasicExpression(field, BasicOperator.LIKE_OP, "%" + address + "%");
    }

    private BasicExpression searchByCountry(int country) {

        BasicField field = new BasicField(HotelDao.ATTR_COUNTRY);
        return new BasicExpression(field, BasicOperator.EQUAL_OP, country);
    }

    /**Métodos de lugares turísticos**/

    @RequestMapping(value = "/touristicPlaces", method = RequestMethod.POST, produces =  MediaType.APPLICATION_JSON_VALUE)
    public EntityResult touristicPlaces(@RequestBody Map<String, Object> req){

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        Map<String, Object> filter = (Map<String, Object>) req.get("filter");

        if(!filter.containsKey("hotel")){
            error.setMessage("Missing hotel attribute");
            return error;
        }

        if(!filter.containsKey("radio")){
            error.setMessage("Missing radio attribute");
            return error;
        }

        Map<String, Object> key = new HashMap<>();
        key.put("id", filter.get("hotel"));

        List<String> attrList = new ArrayList<>();
        attrList.add("latitude");
        attrList.add("longitude");

        EntityResult hotelQuery = iHotelService.hotelQuery(key, attrList);

        if(hotelQuery.getCode() == EntityResult.OPERATION_WRONG){
            return hotelQuery;
        }

        List<BigDecimal> coordenates = (List<BigDecimal>) hotelQuery.get("latitude");
        double latitude = coordenates.get(0).doubleValue();
        coordenates = (List<BigDecimal>) hotelQuery.get("longitude");
        double longitude = coordenates.get(0).doubleValue();

        try{
            double radio = 0;

            try {
                radio = (double) filter.get("radio");
            }catch (Exception e){
                int raux = (int)filter.get("radio");
                radio = raux;
            }

            if(radio <= 0){
                error.setMessage("radio must be greater than 0");
                return error;
            }

            EntityResult touristicPlaces =  getTouristicPlaces(latitude, longitude, radio);
            return touristicPlaces;

        }catch (Exception e){
            e.printStackTrace();
            error.setMessage(e.getMessage());
            return error;
        }
    }

    private static EntityResult getTouristicPlaces(double latitude, double longitude, double distance) throws IOException {
        List<TouristicPlace>touristicPlaceList = parseTouristicPlaces(getTouristicPlacesString(latitude, longitude, distance));

        if(touristicPlaceList.size() == 0){
            EntityResult error = new EntityResultMapImpl();
            error.setMessage("No nearby tourist places founded");
            error.setCode(EntityResult.OPERATION_WRONG);
            return error;
        }

        EntityResult toret = new EntityResultMapImpl();

        for (TouristicPlace tp : touristicPlaceList){
            if(!(tp.getType().equalsIgnoreCase("hostel") || tp.getType().equalsIgnoreCase("hotel")||tp.getType().equalsIgnoreCase("motel")||tp.getType().equalsIgnoreCase("motel"))){
                Hashtable<String, Object> row = new Hashtable<>();
                row.put("latitude", tp.getLatitude());
                row.put("longitude", tp.getLongitude());
                row.put("name", tp.getName());
                row.put("type", tp.getType());
                toret.addRecord(row);
            }
        }

        return toret;
    }

    private static String getTouristicPlacesString(double latitude, double longitude, double distance) throws IOException {
        String formattedLatitude = String.valueOf(latitude);
        String formattedLongitude = String.valueOf(longitude);
        String formattedDistance = String.valueOf(distance);

        String query = "[out:json];" +
                "node(around:" + formattedDistance + "," + formattedLatitude + "," + formattedLongitude + ")" +
                "[tourism];" +
                "out 20;";

        String encodedQuery = URLEncoder.encode(query, "UTF-8");

        String requestUrl = "https://lz4.overpass-api.de/api/interpreter?data=" + encodedQuery;


        HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    private static List<TouristicPlace> parseTouristicPlaces(String json) {
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

    public static String extractTextBetweenBrackets(String input) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return "["+matcher.group(1)+"]";
        } else {
            return input;
        }
    }



    /**Clase Touristic Place**/

    private static class TouristicPlace {
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
    }

    /**Metodo de estadisticas**/

    @RequestMapping(value = "/capacity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult capacity(@RequestBody Map<String, Object> req){
        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        EntityResult toret = new EntityResultMapImpl();

        Map<String, Object> filter = (Map<String, Object>) req.get("filter");
        try {
            Map<String, Object> key = new HashMap<>();
            key.put("id", filter.get("hotel"));

            List<String>attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult hotelQuery = iHotelService.hotelQuery(key, attrList);
            if(hotelQuery.getCode() == EntityResult.OPERATION_WRONG){
                return hotelQuery;
            }
            key = new HashMap<>();
            key.put("hotel", filter.get("hotel"));

            EntityResult roomQuery = iRoomService.roomQuery(key, attrList);
            if(roomQuery.getCode() == EntityResult.OPERATION_WRONG){
                error.setMessage("This hotel doesn't have any room to generate the capacity");
                return error;
            }

            List<Integer>idsRoom = (List<Integer>) roomQuery.get("id");

            key = new HashMap<>();
            key.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                    concatenateExpressionsBooking(idsRoom));

            attrList = new ArrayList<>();
            attrList.add("id");
            attrList.add("room");

            EntityResult bookingQuery = iBookingService.bookingQuery(key, attrList);

            int totalRooms = idsRoom.size();
            int occupiedRooms = 0;
            double percent;
            int bookingCount = 0;

            if(bookingQuery.getCode() == EntityResult.OPERATION_WRONG){
                toret.put("total_rooms", totalRooms);
                toret.put("occupied_rooms", occupiedRooms);
                percent = (occupiedRooms * 100)/totalRooms;
                toret.put("occupation_percent", percent);
                toret.put("total_bookings", bookingCount);
                return toret;
            }

            List<Integer>idsRoomBooking = (List<Integer>) bookingQuery.get("room");
            bookingCount = idsRoomBooking.size();
            HashSet<Integer> roomsNotRepeated = new HashSet<>(idsRoomBooking);
            idsRoomBooking = new ArrayList<>(roomsNotRepeated);

            occupiedRooms = idsRoomBooking.size();
            toret.put("total_rooms", totalRooms);
            toret.put("occupied_rooms", occupiedRooms);
            percent = (occupiedRooms * 100)/totalRooms;
            toret.put("occupation_percent", percent);
            toret.put("total_bookings", bookingCount);

            return toret;
        }catch (Exception e){
            error.setMessage(e.getMessage());
            return error;
        }

    }

    private BasicExpression concatenateExpressionsBooking(List<Integer>idsRoom){
        return new BasicExpression(searchBookingFromRooms(idsRoom), BasicOperator.AND_OP, searchBookingInActualDate());
    }

    private BasicExpression searchBookingFromRooms(List<Integer>idsRoom) {

        BasicField field = new BasicField(BookingDao.ATTR_ROOM);
        BasicExpression bexp = new BasicExpression(field, BasicOperator.EQUAL_OP, idsRoom.get(0));

        for(int i = 1 ; i < idsRoom.size() ; i++){
            BasicExpression bexp2 = new BasicExpression(field, BasicOperator.EQUAL_OP, idsRoom.get(i));
            bexp = new BasicExpression(bexp, BasicOperator.OR_OP, bexp2);
        }

        return bexp;
    }

    private BasicExpression searchBookingInActualDate(){
        Date date = new Date();

        BasicField field = new BasicField(BookingDao.ATTR_ARRIVALDATE);
        BasicExpression bexp = new BasicExpression(field, BasicOperator.LESS_EQUAL_OP, date);

        BasicField field2 = new BasicField(BookingDao.ATTR_DEPARTUREDATE);
        BasicExpression bexp2 = new BasicExpression(field2, BasicOperator.MORE_EQUAL_OP, date);

        return new BasicExpression(bexp, BasicOperator.AND_OP, bexp2);
    }

}
