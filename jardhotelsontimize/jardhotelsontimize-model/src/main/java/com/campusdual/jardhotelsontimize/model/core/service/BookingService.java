package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.model.core.dao.BookingDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private BookingDao bookingDao;

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
            }

        }
        return result;
    }

    @Override
    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.bookingDao, attrMap, keyMap);
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
