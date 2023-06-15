package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IStaffService;
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

    @Autowired
    private JobService jobService;

    @Autowired
    private BankAccountService bankAccountService;

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
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage("The staff member doesn't exist");

            return error;
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
                        checkAttributesStaff(map);
                        EntityResult result = this.daoHelper.insert(this.staffDao, map);
                        result.setMessage("Successful staff insert");
                        return result;
                    }catch (Exception e){
                        EntityResult error = new EntityResultMapImpl();
                        error.setCode(EntityResult.OPERATION_WRONG);
                        error.setMessage(e.getMessage());
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
        EntityResult erStaff = staffQuery(keyMap, attrList);

        if (erStaff.getCode() != EntityResult.OPERATION_WRONG) {
            Map<String,Object>attrMap2 = new HashMap<>();
            if(attrMap.containsKey("bankaccount")){
                attrMap2.put("bankaccount", attrMap.get("bankaccount"));
                attrMap.remove("bankaccount");
            }
            if(attrMap.containsKey("bankaccountformat")){
                attrMap2.put("bankaccountformat", attrMap.get("bankaccountformat"));
                attrMap.remove("bankaccountformat");
            }
            if(attrMap.containsKey("salary")){
                attrMap2.put("salary", attrMap.get("salary"));
                attrMap.remove("salary");
            }
            if(attrMap.containsKey("job")){
                attrMap2.put("job", attrMap.get("job"));
                attrMap.remove("job");
            }

            try{
                checkAttributesStaff(attrMap2);
                //checkAttributesStaff(attrMap2, keyMap);
            }catch (Exception e){
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage(e.getMessage());
                return error;
            }

            EntityResult erPerson = personService.personUpdate(attrMap, keyMap);
            if (erPerson.getCode() == 0) {
                try {
                    this.daoHelper.update(this.staffDao, attrMap2, keyMap);
                }catch (Exception e){
                    EntityResult error = new EntityResultMapImpl();
                    error.setMessage(e.getMessage());
                    error.setCode(EntityResult.OPERATION_WRONG);
                    return error;
                }
                erPerson.setMessage("Successful staff update");
            }else {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage(erPerson.getMessage());
                return error;
            }
            return erPerson;
        }

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);
        error.setMessage("Staff member not found");
        return error;
    }

    private void checkAttributesStaff(Map<String, Object> attrMap, Map<String, Object>keyMap) {
        //TODO
        checkAttributesStaff(attrMap);
    }

    private void checkAttributesStaff(Map<String, Object> attrMap) {

        if(attrMap.containsKey("bankaccountformat")){
            List<String>attrList = new ArrayList<>();
            attrList.add("id");
            Map<String, Object> key = new HashMap<>();
            key.put("id", attrMap.get("bankaccountformat"));

            EntityResult bankQuery = bankAccountService.bankaccountQuery(key, attrList);
            if(bankQuery.getCode() == EntityResult.OPERATION_WRONG){
                throw new RuntimeException("Bank Account Format Not Found");
            }
        }

        if(attrMap.containsKey("bankaccount")){
            //comprobaciones de banco TODO
        }

        if(attrMap.containsKey("salary")){
            Double salary = Double.parseDouble(attrMap.get("salary").toString());
            if(salary <= 0){
                throw new RuntimeException("Salary must be greater than 0");
            }
        }
        if(attrMap.containsKey("job")){
            List<String>attrList = new ArrayList<>();
            attrList.add("id");
            Map<String, Object> key = new HashMap<>();
            key.put("id", attrMap.get("job"));

            EntityResult jobQuery = jobService.jobQuery(key, attrList);
            if(jobQuery.getCode() == EntityResult.OPERATION_WRONG){
                throw new RuntimeException("Job not found");
            }
        }

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
