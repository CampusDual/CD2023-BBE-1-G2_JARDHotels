package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.model.core.dao.BookingDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    public EntityResult bookingQuery(Map<String, Object> keyMap, List<String> attrList) { // TODO recuperar fecha
        EntityResult result = this.daoHelper.query(this.bookingDao, keyMap, attrList);
        return result;
    }

    @Override
    public EntityResult bookingInsert(Map<String, Object> attrMap) {
        return this.daoHelper.insert(this.bookingDao, attrMap);
    }

    @Override
    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.bookingDao, attrMap, keyMap);
    }

    @Override
    public EntityResult bookingDelete(Map<String, Object> keyMap) {
        return this.daoHelper.delete(this.bookingDao, keyMap);
    }
}
