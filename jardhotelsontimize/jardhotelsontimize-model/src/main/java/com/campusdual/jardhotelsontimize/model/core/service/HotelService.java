package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private HotelDao hotelDao;

    @Override
    public EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList) {
        return this.daoHelper.query(this.hotelDao,keyMap,attrList);
    }

    @Override
    public EntityResult hotelInsert(Map<String, Object> attrMap) {
        return this.daoHelper.insert(this.hotelDao,attrMap);
    }

    @Override
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.hotelDao,attrMap,keyMap);
    }

    @Override
    public EntityResult hotelDelete(Map<String, Object> keyMap) {
        return this.daoHelper.delete(this.hotelDao,keyMap);
    }
}
