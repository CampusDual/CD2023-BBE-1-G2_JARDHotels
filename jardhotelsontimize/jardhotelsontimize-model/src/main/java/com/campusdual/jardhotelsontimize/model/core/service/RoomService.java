package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.exceptions.HotelNotFound;
import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.campusdual.jardhotelsontimize.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    public EntityResult roomQuery(Map<String, Object> keyMap, List<String> attrList) {
        return this.daoHelper.query(this.roomDao, keyMap, attrList);
    }

    @Override
    public EntityResult roomInsert(Map<String, Object> attrMap) {
        try {
            return this.daoHelper.insert(this.roomDao, attrMap);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new HotelNotFound("Hotel not found");
//            throw new NotUniqueRoomNumber("A room with this number already exists in the same hotel");
        }
    }

    @Override
    public EntityResult roomUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.roomDao, attrMap, keyMap);
    }

    @Override
    public EntityResult roomDelete(Map<String, Object> keyMap) {
        return this.daoHelper.delete(this.roomDao, keyMap);
    }
}
