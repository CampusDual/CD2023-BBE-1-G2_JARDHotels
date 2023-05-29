package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("GuestService")
public class GuestService implements IGuestService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private GuestDao guestDao;

    @Override
    public EntityResult guestQuery(Map<String, Object> keyMap, List<String> attrList) {
        EntityResult result = this.daoHelper.query(this.guestDao, keyMap, attrList);
        if(result.toString().contains("id"))
            result.setMessage("The guest has been found");
        else{
            result.setMessage("The guest doesn't exists");
            result.setColumnSQLTypes(new HashMap());
        }
        return result;
    }

    @Override
    public EntityResult guestInsert(Map<String, Object> attrMap) {

        List<String>attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = guestQuery(attrMap, attrList);
        try{
            result = this.daoHelper.insert(this.guestDao, attrMap);
        }catch (Exception e){
            //TODO comprobar email documentacion telefono y país
        }
        return result;
    }

    @Override
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.guestDao, attrMap, keyMap);
        //TODO comprobar email documentacion telefono y país
    }

    @Override
    public EntityResult guestDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.guestDao, keyMap, attrList);
        EntityResult result = this.daoHelper.delete(this.guestDao, keyMap);

        if(query.toString().contains("id"))
            result.setMessage("Successful guest delete");
        else{
            result.setMessage("Guest not found");
            result.setColumnSQLTypes(new HashMap());
        }

        return result;
    }
}
