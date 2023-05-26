package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.exceptions.HotelNotFound;
import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList) { // TODO comprobar si tiene cuerpo

        EntityResult result = this.daoHelper.query(this.hotelDao,keyMap,attrList);

        return result;
    }

    @Override
    public EntityResult hotelInsert(Map<String, Object> attrMap) {

        EntityResult result = this.daoHelper.insert(this.hotelDao,attrMap);
        result.setMessage("Successful hotel insertion");
        return result;
    }

    @Override
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        EntityResult result = this.daoHelper.update(this.hotelDao,attrMap,keyMap);
        if (result.getCode() == 0)
            result.setMessage("Successful hotel update");
        if (result.getCode() == 2) {
            result.setMessage("Hotel not found");
        }
        return result;
    }

    @Override
    public EntityResult hotelDelete(Map<String, Object> keyMap) { // TODO arreglar comprobacion

        List<String> attrList = new ArrayList<>();
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.hotelDao,keyMap,attrList);

        EntityResult result = this.daoHelper.delete(this.hotelDao,keyMap);
        if (query.getCode() != 0)
        {result.setMessage("Error");}
        else
            result.setMessage("Correcto");
        return result;
    }
}
