package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.campusdual.jardhotelsontimize.api.core.service.IStaffService;
import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
import com.campusdual.jardhotelsontimize.model.core.dao.StaffDao;
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
@Service("StaffService")
public class StaffService implements IStaffService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private PersonService personService;

    @Override
    public EntityResult staffQuery(Map<String, Object> keyMap, List<String> attrList) {

        List<String> attrList2 = new ArrayList<>();
        attrList2.add("id");
        if (attrList.contains("bankaccount")){
            attrList2.add("bankaccount");
            attrList.remove("bankaccount");
        }
        if (attrList.contains("bankaccountformat")){
            attrList2.add("bankaccountformat");
            attrList.remove("bankaccountformat");
        }
        if (attrList.contains("salary")){
            attrList2.add("salary");
            attrList.remove("salary");
        }
        if (attrList.contains("job")){
            attrList2.add("job");
            attrList.remove("job");
        }
        EntityResult erStaff = this.daoHelper.query(this.staffDao, keyMap, attrList2);
        if (!erStaff.toString().contains("id")) {
            erStaff.setCode(EntityResult.OPERATION_WRONG);
            erStaff.setMessage("The staff member doesn't exist");
            return erStaff;
        }

        EntityResult erPerson = this.personService.personQuery(keyMap, attrList);
        EntityResult er = new EntityResultMapImpl();

        for (int i = 0; i < ((List<Integer>) erPerson.get("id")).size(); i++) {
            if (((List<Integer>) erStaff.get("id")).contains(((List<Integer>) erPerson.get("id")).get(i))) {

                Map<String, Object> m = new HashMap<>();
                for (String s : attrList) {
                    m.put(s, ((List<Object>) erPerson.get(s)).get(i));
                }
                List<Integer> listIds = (List<Integer>) erStaff.get("id");
                int position = 0;
                for (int j = 0;j<listIds.size();j++){
                    if(listIds.get(j)==((List<Integer>) erPerson.get("id")).get(i)){
                        position=j;
                        break;

                    }
                }
                for (String s : attrList2) {
                    if (!s.equals("id")) {
                        m.put(s, ((List<Object>) erStaff.get(s)).get(position));
                    }
                }
                er.addRecord(m);
            }
        }

        return er;
    }

    @Override
    public EntityResult staffInsert(Map<String, Object> attrMap) {
        if(attrMap.get("bankaccount") == null || attrMap.get("bankaccountformat") == null || attrMap.get("salary") == null || attrMap.get("job") == null){
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage("All attributes must be filled");
            return error;
        }
        Map<String,Object> copy = new HashMap<>(attrMap);
        copy.remove("bankaccount");
        copy.remove("bankaccountformat");
        copy.remove("salary");
        copy.remove("job");
        if(attrMap.get("id") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", attrMap.get("id"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            EntityResult resultId = personService.personQuery(map, attrList);
            if (resultId.getCode()==0){
                EntityResult queryStaff = staffQuery(map,attrList);
                if (queryStaff.getCode()==1){
                    try {
                        map.put("bankaccount", attrMap.get("bankaccount"));
                        map.put("bankaccountformat", attrMap.get("bankaccountformat"));
                        map.put("salary", attrMap.get("salary"));
                        map.put("job", attrMap.get("job"));
                        EntityResult result = this.daoHelper.insert(this.staffDao, map);
                        result.setMessage("Successful staff insert");
                        return result;
                    }catch (Exception e){
                        EntityResult error = new EntityResultMapImpl();
                        error.setCode(EntityResult.OPERATION_WRONG);
                        error.setMessage("Staff insert fail");
                        return error;
                    }

                }else {
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("Repeated staff member");
                    return error;
                }

            }else {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage("Person not found");
                return error;
            }
        }
        EntityResult result = personService.personInsert(copy);
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
            map.put("bankaccount",attrMap.get("bankaccount"));
            map.put("bankaccountformat",attrMap.get("bankaccountformat"));
            map.put("salary",attrMap.get("salary"));
            map.put("job",attrMap.get("job"));
            this.daoHelper.insert(this.staffDao, map);
            result.setMessage("Successful staff member insertion");
        }
        return result;
    }

    @Override
    public EntityResult staffUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult erGuest = staffQuery(keyMap, attrList);
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
    public EntityResult staffDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult erGuest = this.daoHelper.query(this.staffDao, keyMap, attrList);
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
