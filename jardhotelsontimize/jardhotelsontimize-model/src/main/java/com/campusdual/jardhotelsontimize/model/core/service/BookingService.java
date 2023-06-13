package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.model.core.dao.BookingDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private RoomService roomService;

    @Override
    public EntityResult bookingQuery(Map<String, Object> keyMap, List<String> attrList) {
        EntityResult result = this.daoHelper.query(this.bookingDao, keyMap, attrList);
        if (result.toString().contains("id")) result.setMessage("");
        else {
            result.setMessage("The booking doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }

    @Override
    public EntityResult bookingInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();

        Map<String, Object>keyMapRoom = new HashMap<>();
        keyMapRoom.put("id", Integer.parseInt(attrMap.get("room").toString()));

        List<String>attrListRoom = new ArrayList<>();
        attrListRoom.add("price");

        EntityResult roomQuery = roomService.roomQuery(keyMapRoom, attrListRoom);

        if(roomQuery.toString().contains("price")){
            if (!attrMap.containsKey("totalprice") && attrMap.containsKey("arrivaldate") && attrMap.containsKey("departuredate")) {
                double price = Double.parseDouble(((List<BigDecimal>) roomQuery.get("price")).get(0).toString());
                attrMap.put("totalprice", calculateTotalPrice(attrMap.get("arrivaldate").toString(), attrMap.get("departuredate").toString(), price));
            }
        }

        try {
            result = this.daoHelper.insert(this.bookingDao, attrMap);
            result.setMessage("Successful booking insertion");
        } catch (Exception e) {

            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("Arrival date must be greater than or equal to current date")) {
                result.setMessage("Arrival date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Departure date must be greater than arrival date")) {
                result.setMessage("Departure date must be greater than arrival date");
            } else if (e.getMessage().contains("The date range overlaps with the dates of an existing booking")) {
                result.setMessage("The date range overlaps with the dates of an existing booking");
            } else if (e.getMessage().contains("The total price can't be lower than 0")) {
                result.setMessage("The total price can't be lower than 0");
            } else if (e.getMessage().contains("booking_room_fkey")) {
                result.setMessage("Room not found");
            } else if (e.getMessage().contains("booking_guest_fkey")) {
                result.setMessage("Guest not found");
            } else if(!roomQuery.contains("price")) {
                result.setMessage("Room not found");
            } else {
                result.setMessage(e.getMessage());
            }

        }
        return result;
    }

    @Override
    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        EntityResult result = new EntityResultMapImpl();

        //sacar el precio de la habitación

        Map<String, Object>keyMapRoom = new HashMap<>();
        keyMapRoom.put("id", Integer.parseInt(attrMap.get("room").toString()));

        List<String>attrListRoom = new ArrayList<>();
        attrListRoom.add("price");

        EntityResult roomQuery = roomService.roomQuery(keyMapRoom, attrListRoom);

        if(roomQuery.toString().contains("price")){
            if (!attrMap.containsKey("totalprice")) {
                double price = Double.parseDouble(((List<BigDecimal>) roomQuery.get("price")).get(0).toString());
                attrMap.put("totalprice", calculateTotalPrice(attrMap.get("arrivaldate").toString(), attrMap.get("departuredate").toString(), price));
            }
        }

        try{
            result = this.daoHelper.update(this.bookingDao, attrMap, keyMap);

            if (result.getCode() == 2) {
                result.setMessage("Booking not found");
                result.setCode(EntityResult.OPERATION_WRONG);
            } else {
                result.setMessage("Successful booking update");
            }
        }catch (Exception e){
            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("Arrival date must be greater than or equal to current date")) {
                result.setMessage("Arrival date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Departure date must be greater than arrival date")) {
                result.setMessage("Departure date must be greater than arrival date");
            } else if (e.getMessage().contains("The date range overlaps with the dates of an existing booking")) {
                result.setMessage("The date range overlaps with the dates of an existing booking");
            } else if (e.getMessage().contains("The total price can't be lower than 0")) {
                result.setMessage("The total price can't be lower than 0");
            } else if (e.getMessage().contains("Changing the guest is not allowed")){
                result.setMessage("Changing the guest is not allowed");
            } else if(e.getMessage().contains("Changing the room to a different hotel is not allowed")){
                result.setMessage("Changing the room to a different hotel is not allowed");
            } else if (e.getMessage().contains("booking_room_fkey")) {
                result.setMessage("Room not found");
            } else {
                result.setMessage(e.getMessage());
            }
        }

        return result;
    }

    private static double calculateTotalPrice(String arrivalDate, String departureDate, double price) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        double totalPrice = 0;

        try {
            Date arrival = format.parse(arrivalDate.substring(0, 10));
            Date departure = format.parse(departureDate.substring(0, 10));

            long diffInMillies = Math.abs(departure.getTime() - arrival.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            totalPrice = diffInDays * price;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalPrice;
    }

    @Override
    public EntityResult bookingDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("totalprice");
        attrList.add("arrivaldate");

        EntityResult query = this.daoHelper.query(this.bookingDao, keyMap, attrList);

        EntityResult result = this.daoHelper.delete(this.bookingDao, keyMap);

        if (query.toString().contains("id")) {
            result.setMessage("Successful booking delete");
            double price = Double.parseDouble(((List<BigDecimal>) query.get("totalprice")).get(0).toString());
            result.put("refund", calculateRefund(price, query.get("arrivaldate").toString()));
        } else {
            result.setMessage("Booking not found");
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    private double calculateRefund(double totalprice, String arrivalDate) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd']'");
        LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate, formatter);

        long daysUntilArrival = ChronoUnit.DAYS.between(currentDate, parsedArrivalDate);

        if (daysUntilArrival > 7) {
            return totalprice;
        } else if (daysUntilArrival > 1) {
            return totalprice / 2;
        } else {
            return 0;
        }
    }
}
