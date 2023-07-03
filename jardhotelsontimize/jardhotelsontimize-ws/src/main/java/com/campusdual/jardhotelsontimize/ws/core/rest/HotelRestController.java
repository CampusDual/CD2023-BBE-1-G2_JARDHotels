package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.ICountryService;
import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.ontimize.jee.common.db.SQLStatementBuilder.*;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotels")
public class HotelRestController extends ORestController<IHotelService> {

    @Autowired
    private IHotelService iHotelService;

    @Autowired
    private ICountryService iCountryService;

    @Override
    public IHotelService getService() {
        return this.iHotelService;
    }

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
}
