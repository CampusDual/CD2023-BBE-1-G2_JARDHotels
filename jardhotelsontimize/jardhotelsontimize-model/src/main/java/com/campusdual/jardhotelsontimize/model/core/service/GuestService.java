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
        EntityResult resultGuest = this.daoHelper.query(this.guestDao, keyMap, attrList2);
        if (!resultGuest.toString().contains("id")) {
            resultGuest.setCode(EntityResult.OPERATION_WRONG);
            resultGuest.setMessage("The guest doesn't exist");
            return resultGuest;
        }

        List<Integer> ids = (List<Integer>) resultGuest.get("id");
        List<List<Object>> listList = new ArrayList<>();
        for (String s : attrList) {
            listList.add(new ArrayList<>());
        }
        for (int id : ids) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            EntityResult res = this.personService.personQuery(map, attrList);
            for (int i = 0; i < attrList.size(); i++) {
                List<Object> l = (List<Object>) res.get(attrList.get(i));
                listList.get(i).add(l.get(0));
            }
        }
        EntityResult er = new EntityResultMapImpl();
        for (int i = 0; i < attrList.size(); i++) {
            er.put(attrList.get(i), listList.get(i));
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
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        EntityResult result = personService.personUpdate(attrMap, keyMap);
        if (result.getCode() == 0) {
            result.setMessage("Successful guest update");
        }
        return result;
    }

    @Override
    public EntityResult guestDelete(Map<String, Object> keyMap) {

        EntityResult result = personService.personDelete(keyMap);
        if (result.getCode() == 0) {
            result.setMessage("Successful guest delete");
        }
        return result;
    }
}
