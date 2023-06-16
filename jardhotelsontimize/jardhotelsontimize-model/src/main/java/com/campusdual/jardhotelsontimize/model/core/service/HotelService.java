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
        if(result.toString().contains("id")) {
            result.setMessage("");

        } else {
            result.setMessage("The hotel doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
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
            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("Stars must be between one and five")) {
                result.setMessage("Stars must be between one and five");
            } else if (e.getMessage().contains("\"hotel\" violates foreign key constraint \"hotel_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
            result.setCode(EntityResult.OPERATION_WRONG);
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
            if (result.getCode() == 2) {
                result.setMessage("Hotel not found");
                result.setCode(EntityResult.OPERATION_WRONG);
            }
        }catch (Exception e){
            if (e.getMessage().contains("Stars must be between one and five")) {
                result.setMessage("Stars must be between one and five");
            } else if (e.getMessage().contains("\"hotel\" violates foreign key constraint \"hotel_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
            result.setCode(EntityResult.OPERATION_WRONG);
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
        else {
            result.setMessage("Hotel not found");
            result.setCode(EntityResult.OPERATION_WRONG);
        }
        return result;
    }
}
