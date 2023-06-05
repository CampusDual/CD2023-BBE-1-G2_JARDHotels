package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
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
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private HotelDao hotelDao;

    @Override
    public EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList) {

        EntityResult result = this.daoHelper.query(this.hotelDao,keyMap,attrList);
        if(result.toString().contains("id"))
            result.setMessage("The hotel has been found");
        else {
            result.setMessage("The hotel doesn't exist");
            result.setColumnSQLTypes(new HashMap());
        }

        return result;
    }

    @Override
    public EntityResult hotelInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();

        try{
            result = this.daoHelper.insert(this.hotelDao,attrMap);
            result.setMessage("Successful hotel insertion");
        }catch (Exception e){
            result.setCode(0);
            result.setMessage("The country doesn't exist");

        }

        return result;
    }

    @Override
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        EntityResult result = new EntityResultMapImpl();

        try {
            result = this.daoHelper.update(this.hotelDao,attrMap,keyMap);
            if (result.getCode() == 0)
                result.setMessage("Successful hotel update");
            if (result.getCode() == 2)
                result.setMessage("Hotel not found");
        }catch (Exception e){
            result.setMessage("The country doesn't exist");
            result.setCode(0);
        }

        return result;
    }

    @Override
    public EntityResult hotelDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.hotelDao,keyMap,attrList);

        EntityResult result = this.daoHelper.delete(this.hotelDao,keyMap);

        if (query.toString().contains("id"))
            result.setMessage("Successful hotel delete");
        else
            result.setMessage("Hotel not found");
        return result;
    }
}
