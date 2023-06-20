package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.campusdual.jardhotelsontimize.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private RoomDao roomDao;


    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomQuery(Map<String, Object> keyMap, List<String> attrList) {
        EntityResult result = this.daoHelper.query(this.roomDao, keyMap, attrList);

        if (result.toString().contains("id") || result.toString().contains("ID"))
            result.setMessage("");
        else {
            result.setMessage("The room doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomInsert(Map<String, Object> attrMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = roomQuery(attrMap, attrList);
        try {
            result = this.daoHelper.insert(this.roomDao, attrMap);
            result.setMessage("Successful room insertion");
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);

            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("room_hotel_fkey"))
                result.setMessage("Hotel not found");
            else if (e.getMessage().contains("Repeated number in hotel"))
                result.setMessage("Repeated number in hotel");
            else if (e.getMessage().contains("Number must be over zero"))
                result.setMessage("Number must be over zero");
            else if (e.getMessage().contains("Capacity must be over zero"))
                result.setMessage("Capacity must be over zero");
            else if (e.getMessage().contains("The price must be greater than 0"))
                result.setMessage("The price must be greater than 0");
            else result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = roomQuery(keyMap, attrList);
        if (result.getMessage().contains("The room doesn't exist")) {
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }
        try {
            result = this.daoHelper.update(this.roomDao, attrMap, keyMap);
            result.setMessage("Successful room update");
        } catch (Exception e) {
            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("Change the hotel of a room is not allowed"))
                result.setMessage("Change the hotel of a room is not allowed");
            else if (e.getMessage().contains("Number must be over zero"))
                result.setMessage("Number must be over zero");
            else if (e.getMessage().contains("Repeated number in hotel"))
                result.setMessage("Repeated number in hotel");
            else if (e.getMessage().contains("Capacity must be over zero"))
                result.setMessage("Capacity must be over zero");
            else if (e.getMessage().contains("The price must be greater than 0"))
                result.setMessage("The price must be greater than 0");
            else result.setMessage(e.getMessage());

            result.setColumnSQLTypes(new HashMap<>());
        }

        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");

        EntityResult query = this.daoHelper.query(this.roomDao, keyMap, attrList);

        EntityResult result = this.daoHelper.delete(this.roomDao, keyMap);

        if (query.toString().contains("id"))
            result.setMessage("Successful room delete");
        else {
            result.setMessage("Room not found");
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }
}
