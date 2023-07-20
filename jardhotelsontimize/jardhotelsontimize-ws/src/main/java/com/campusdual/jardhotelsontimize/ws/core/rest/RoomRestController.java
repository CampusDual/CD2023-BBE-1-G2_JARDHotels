package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.campusdual.jardhotelsontimize.model.core.dao.RoomDao;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.db.SQLStatementBuilder.*;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/rooms")
public class RoomRestController extends ORestController<IRoomService> {

    @Autowired
    private IRoomService iRoomService;

    @Autowired
    private IHotelService iHotelService;

    @Autowired
    private IBookingService iBookingService;

    @Override
    public IRoomService getService() {
        return this.iRoomService;
    }

    @RequestMapping(value = "/roomDoor", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult roomDoor(@RequestBody Map<String, Object> req){
        Map<String, Object> filter = (Map<String, Object>) req.get("filter");

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        if(filter.get("code") == null){
            error.setMessage("Missing code");
            return error;
        }

        if(filter.get("room") == null){
            error.setMessage("Missing room");
            return error;
        }

        try {
            Map<String, Object>key = new HashMap<>();
            key.put("id", filter.get("room"));

            List<String>attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult roomQuery = iRoomService.roomQuery(key, attrList);
            if(roomQuery.getCode() == EntityResult.OPERATION_WRONG){
                error.setMessage(roomQuery.getMessage());
                return error;
            }

            key = new HashMap<>();
            key.put("code", filter.get("code"));

            attrList.add("room");
            attrList.add("arrivaldate");
            attrList.add("departuredate");
            attrList.add("chekindate");
            attrList.add("id");

            EntityResult bookingQuery = iBookingService.bookingQuery(key, attrList);

            if(bookingQuery.getCode() == EntityResult.OPERATION_WRONG){
                error.setMessage(bookingQuery.getMessage());
                return error;
            }

            List<Integer>rooms = (List<Integer>) bookingQuery.get("room");

            int roomToPass = (int) filter.get("room");
            int roomFromBooking = rooms.get(0);

            if(roomToPass == roomFromBooking){
                List<Date>arrivalDates = (List<Date>) bookingQuery.get("arrivaldate");
                List<Date>departureDates = (List<Date>) bookingQuery.get("departuredate");

                Date now = new Date();
                if (now.compareTo(arrivalDates.get(0)) >= 0 && now.compareTo(departureDates.get(0)) <= 0) {
                    EntityResult ok = new EntityResultMapImpl();
                    ok.setMessage("Access granted");

                    List<Date>checkinsDate = (List<Date>) bookingQuery.get("checkindate");
                    List<Integer>ids = (List<Integer>) bookingQuery.get("id");
                    if (checkinsDate == null){
                        key = new HashMap<>();
                        key.put("id", ids.get(0));
                        Map<String, Object>attrMap = new HashMap<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateNow = sdf.format(now);
                        attrMap.put("checkindate", dateNow);
                        iBookingService.bookingUpdate(attrMap, key);
                    }
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

    @RequestMapping(value = "/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult filter(@RequestBody Map<String, Object> req) {
        try {
            List<String> columns = (List<String>) req.get("columns");
            Map<String, Object> filter = (Map<String, Object>) req.get("filter");
            Map<String, Object> key = new HashMap<>();

            key.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                    concatenateExpressions(filter));

            return iRoomService.roomQuery(key, columns);

        } catch (Exception e) {
            EntityResult res = new EntityResultMapImpl();
            res.setCode(EntityResult.OPERATION_WRONG);

            // excepciones capacidad
            if (e.getMessage().contains("Capacity must be greater than 0")) {
                res.setMessage("Capacity must be greater than 0");
            } else if (e.getMessage().contains("Capacity max must be greater than capacity min")) {
                res.setMessage("Capacity max must be greater than capacity min");
            } else if (e.getMessage().contains("Capacity must be a whole number")) {
                res.setMessage("Capacity must be a whole number");
            }

            //excepciones hotel
            if (e.getMessage().contains("Hotel must be a whole number")) {
                res.setMessage("Hotel must be a whole number");
            } else if (e.getMessage().contains("Hotel must exist")) {
                res.setMessage("Hotel must exist");
            }

            // excepciones precio
            if (e.getMessage().contains("Price must be greater than 0")) {
                res.setMessage("Price must be greater than 0");
            } else if (e.getMessage().contains("Price max must be greater than price min")) {
                res.setMessage("Price max must be greater than price min");
            } else if (e.getMessage().contains("Price must be a decimal number")) {
                res.setMessage("Price must be a decimal number");
            }

            return res;
        }
    }

    private SQLStatementBuilder.BasicExpression concatenateExpressions(Map<String, Object> filter) {

        // filtro descripción
        String description = "";

        if (filter.get("description") != null) {
            description = (String) filter.get("description");
        }

        SQLStatementBuilder.BasicExpression bexp = searchLikeDescription(description);

        // filtro capacidad
        int capacityMin = 1;
        int capacityMax;
        try {
            if (filter.get("capacity_min") != null) {
                capacityMin = (int) filter.get("capacity_min");
            }
            if (capacityMin < 1) {
                throw new RuntimeException("Capacity must be greater than 0");
            }
            bexp = new SQLStatementBuilder.BasicExpression(bexp, SQLStatementBuilder.BasicOperator.AND_OP,
                    searchByMinCapacity(capacityMin));
            if (filter.get("capacity_max") != null) {
                capacityMax = (int) filter.get("capacity_max");
                if (capacityMax < capacityMin) {
                    throw new RuntimeException("Capacity max must be greater than capacity min");
                }
                bexp = new SQLStatementBuilder.BasicExpression(bexp, SQLStatementBuilder.BasicOperator.AND_OP,
                        searchByMaxCapacity(capacityMax));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Capacity must be greater than 0")) {
                throw new RuntimeException("Capacity must be greater than 0");
            } else if (e.getMessage().contains("Capacity max must be greater than capacity min")) {
                throw new RuntimeException("Capacity max must be greater than capacity min");
            }
            throw new RuntimeException("Capacity must be a whole number");
        }

        // filtro hotel
        if (filter.get("hotel") != null) {

            try {
                int hotel = (int) filter.get("hotel");
            } catch (Exception e) {
                throw new RuntimeException("Hotel must be a whole number");
            }

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", filter.get("hotel"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult entityResult = iHotelService.hotelQuery(keyMap, attrList);

            if (entityResult.getMessage().contains("The hotel doesn't exist")) {
                throw new RuntimeException("Hotel must exist");
            }

            bexp = new SQLStatementBuilder.BasicExpression(bexp, SQLStatementBuilder.BasicOperator.AND_OP,
                    searchByHotel((int) filter.get("hotel")));
        }

        //filtro precio
        double priceMin = 0;
        double priceMax;
        try {
            if (filter.get("price_min") != null) {
                boolean checkDouble = false;
                try {
                    priceMin = (int) filter.get("price_min");
                } catch (Exception e) {
                    checkDouble = true;
                }
                if (checkDouble) {
                    priceMin = (double) filter.get("price_min");
                }
            }
            if (priceMin < 0) {
                throw new RuntimeException("Price must be greater than 0");
            }
            bexp = new SQLStatementBuilder.BasicExpression(bexp, SQLStatementBuilder.BasicOperator.AND_OP,
                    searchByMinPrice(priceMin));
            if (filter.get("price_max") != null) {
                priceMax = (double) filter.get("price_max");
                if (priceMax < priceMin) {
                    throw new RuntimeException("Price max must be greater than price min");
                }
                bexp = new SQLStatementBuilder.BasicExpression(bexp, SQLStatementBuilder.BasicOperator.AND_OP,
                        searchByMaxPrice(priceMax));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Price must be greater than 0")) {
                throw new RuntimeException("Price must be greater than 0");
            } else if (e.getMessage().contains("Price max must be greater than price min")) {
                throw new RuntimeException("Price max must be greater than price min");
            }
            throw new RuntimeException("Price must be a decimal number");
        }
        return bexp;
    }

    private BasicExpression searchByMaxCapacity(int capacityMax) {
        BasicField field = new BasicField(RoomDao.ATTR_CAPACITY);
        return new BasicExpression(field, BasicOperator.LESS_EQUAL_OP, capacityMax);
    }

    private BasicExpression searchByMinCapacity(int capacityMin) {
        BasicField field = new BasicField(RoomDao.ATTR_CAPACITY);
        return new BasicExpression(field, BasicOperator.MORE_EQUAL_OP, capacityMin);
    }

    private BasicExpression searchLikeDescription(String description) {
        BasicField field = new BasicField(RoomDao.ATTR_DESCRIPTION);
        return new BasicExpression(field, BasicOperator.LIKE_OP, "%" + description + "%");
    }

    private BasicExpression searchByHotel(int hotel) {

        BasicField field = new BasicField(RoomDao.ATTR_HOTEL);
        return new BasicExpression(field, BasicOperator.EQUAL_OP, hotel);
    }

    private BasicExpression searchByMaxPrice(double priceMax) {
        BasicField field = new BasicField(RoomDao.ATTR_PRICE);
        return new BasicExpression(field, BasicOperator.LESS_EQUAL_OP, priceMax);
    }

    private BasicExpression searchByMinPrice(double priceMin) {
        BasicField field = new BasicField(RoomDao.ATTR_PRICE);
        return new BasicExpression(field, BasicOperator.MORE_EQUAL_OP, priceMin);
    }
}
