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
        if (result.toString().contains("id")) result.setMessage("The booking has been found");
        else {
            result.setMessage("The booking doesn't exists");
            result.setColumnSQLTypes(new HashMap());
        }
        return result;
    }

    @Override
    public EntityResult bookingInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();

        try {
            result = this.daoHelper.insert(this.bookingDao, attrMap);
            result.setMessage("Successful booking insertion");
        } catch (Exception e) {

            result.setCode(0);
            if (e.getMessage().contains("Check-in date must be greater than or equal to current date")) {
                result.setMessage("Check-in date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Check-out date must be greater than check-in date")) {
                result.setMessage("Check-out date must be greater than check-in date");
            } else if (e.getMessage().contains("The date range overlaps with the dates of an existing booking")) {
                result.setMessage("The date range overlaps with the dates of an existing booking");
            } else if (e.getMessage().contains("The total price can't be lower than 0")) {
                result.setMessage("The total price can't be lower than 0");
            } else if (e.getMessage().contains("booking_room_fkey")) {
                result.setMessage("Room not found");
            } else if (e.getMessage().contains("booking_guest_fkey")) {
                result.setMessage("Guest not found");
            } else result.setMessage(e.getMessage());

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
                Double price = Double.parseDouble(((List<BigDecimal>) roomQuery.get("price")).get(0).toString());
                attrMap.put("totalprice", calculateTotalPrice(attrMap.get("checkindate").toString(), attrMap.get("checkoutdate").toString(), price));
            }
        }

        try{
            result = this.daoHelper.update(this.bookingDao, attrMap, keyMap);
            result.setMessage("Successful booking update");
        }catch (Exception e){
            if (e.getMessage().contains("Check-in date must be greater than or equal to current date")) {
                result.setMessage("Check-in date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Check-out date must be greater than check-in date")) {
                result.setMessage("Check-out date must be greater than check-in date");
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
            } else result.setMessage(e.getMessage());
        }

        return result;
    }

    private static double calculateTotalPrice(String checkinDate, String checkoutDate, double price) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        double totalPrice = 0;

        try {
            Date checkin = format.parse(checkinDate.substring(0, 10));
            Date checkout = format.parse(checkoutDate.substring(0, 10));

            long diffInMillies = Math.abs(checkout.getTime() - checkin.getTime());
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

        EntityResult query = this.daoHelper.query(this.bookingDao, keyMap, attrList);

        EntityResult result = this.daoHelper.delete(this.bookingDao, keyMap);

        if (query.toString().contains("id"))
            result.setMessage("Successful booking delete");
        else
            result.setMessage("Booking not found");

        return result;
    }
}
