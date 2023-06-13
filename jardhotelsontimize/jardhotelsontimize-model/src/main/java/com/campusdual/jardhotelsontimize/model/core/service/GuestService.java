package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
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
@Service("GuestService")
public class GuestService implements IGuestService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private GuestDao guestDao;

    @Autowired
    private PersonService personService;

    @Override
    public EntityResult guestQuery(Map<String, Object> keyMap, List<String> attrList) {

        List<String> attrList2 = new ArrayList<>();
        attrList2.add("id");
        EntityResult erGuest = this.daoHelper.query(this.guestDao, keyMap, attrList2);
        if (!erGuest.toString().contains("id")) {
            erGuest.setCode(EntityResult.OPERATION_WRONG);
            erGuest.setMessage("The guest doesn't exist");
            return erGuest;
        }

        EntityResult erPerson = this.personService.personQuery(keyMap, attrList);
        EntityResult er = new EntityResultMapImpl();

        for (int i = 0; i < ((List<Integer>) erPerson.get("id")).size(); i++) {
            if (((List<Integer>) erGuest.get("id")).contains(((List<Integer>) erPerson.get("id")).get(i))) {

                Map<String, Object> m = new HashMap<>();
                for (String s : attrList) {
                    m.put(s, ((List<Object>) erPerson.get(s)).get(i));
                }
                er.addRecord(m);
            }
        }

        return er;
    }

    @Override
    public EntityResult guestInsert(Map<String, Object> attrMap) {

        EntityResult result = personService.personInsert(attrMap);
        if (result.getCode() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("documentation", attrMap.get("documentation"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            EntityResult resultId = personService.personQuery(map, attrList);
            List<Integer> ids = (List<Integer>) resultId.get("id");
            int id = ids.get(0);
            map = new HashMap<>();
            map.put("id", id);
            this.daoHelper.insert(this.guestDao, map);
            result.setMessage("Successful guest insertion");
        }
        return result;
    }

    @Override
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) { // TODO

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult erGuest = guestQuery(keyMap, attrList);
        if (erGuest.toString().contains("id")) {
            EntityResult erPerson = personService.personUpdate(attrMap, keyMap);
            if (erPerson.getCode() == 0) {
                erPerson.setMessage("Successful guest update");
            }
            return erPerson;
        }

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);
        error.setMessage("Guest not found");
        return error;
    }

    @Override
    public EntityResult guestDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult erGuest = this.daoHelper.query(this.guestDao, keyMap, attrList);
        if (erGuest.toString().contains("id")) {
                EntityResult erPerson = personService.personDelete(keyMap);
            if (erPerson.getCode() == 0) {
                erPerson.setMessage("Successful guest delete");
            }
            return erPerson;
        }

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);
        error.setMessage("Guest not found");
        return error;
    }
}
