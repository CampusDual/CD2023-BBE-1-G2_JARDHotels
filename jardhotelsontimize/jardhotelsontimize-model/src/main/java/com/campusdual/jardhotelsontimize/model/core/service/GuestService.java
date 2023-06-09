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
        if (result.toString().contains("id")) result.setMessage("The guest has been found");
        else {
            result.setMessage("The guest doesn't exist");
            result.setColumnSQLTypes(new HashMap());
        }
        return result;
    }

    @Override
    public EntityResult guestInsert(Map<String, Object> attrMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = guestQuery(attrMap, attrList);
        try {
            result = this.daoHelper.insert(this.guestDao, attrMap);
            result.setMessage("Successful guest insertion");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (e.getMessage().contains("verify_documentation_spain()")) {
                if (e.getMessage().contains(("The spanish DNI must have 9 characters")))
                    result.setMessage("The spanish DNI must have 9 characters");
                else if (e.getMessage().contains(("The 8 first characters of a spanish DNI must be numbers")))
                    result.setMessage("The 8 first characters of a spanish DNI must be numbers");
                else if (e.getMessage().contains(("The last character of a spanish DNI must be a letter")))
                    result.setMessage("The last character of a spanish DNI must be a letter");
                else if (e.getMessage().contains(("The letter of the spanish DNI is incorrect")))
                    result.setMessage("The letter of the spanish DNI is incorrect");
                else result.setMessage("The format of the spanish DNI is incorrect");
            } else if (e.getMessage().contains("verify_phone_spain()")) {
                if (e.getMessage().contains(("The format for the spanish phone is incorrect. It must be +34 and 9 numbers")))
                    result.setMessage("The format for the spanish phone is incorrect. It must be +34 and 9 numbers");
                else result.setMessage("The format for the spanish phone is incorrect");
            } else if (e.getMessage().contains("verify_documentation_united_states()")) {
                if (e.getMessage().contains(("El format for the passport in United States is incorrect")))
                    result.setMessage("El format for the passport in United States is incorrect. It must be 9 characters");
                else result.setMessage("The format for the passport in United States is incorrect");
            } else if (e.getMessage().contains("verify_phone_united_states()")) {
                if (e.getMessage().contains(("The format for the phone in United States is incorrect")))
                    result.setMessage("The format for the phone in United States is incorrect. It must be +1 and 10 numbers");
                else result.setMessage("The format for the phone in United States is incorrect");
            } else if (e.getMessage().contains("verify_documentation_united_kingdom()")) {
                result.setMessage("The format for the passport in United Kingdom is incorrect");
            } else if (e.getMessage().contains("verify_phone_united_kingdom()")) {
                result.setMessage("The format for the phone in United Kingdom is incorrect");
            } else if (e.getMessage().contains("verify_documentation_france()")) {
                result.setMessage("The format for the card in France is incorrect");
            } else if (e.getMessage().contains("verify_phone_france()")) {
                result.setMessage("The format for the phone in France is incorrect");
            } else if (e.getMessage().contains("verify_documentation_germany()")) {
                result.setMessage("The format for the documentation in Germany is incorrect");
            } else if (e.getMessage().contains("verify_phone_germany()")) {
                result.setMessage("The format for the phone in Germany is incorrect");
            } else if (e.getMessage().contains("verify_documentation_portugal()")) {
                result.setMessage("The format for the documentation in Portugal is incorrect");
            } else if (e.getMessage().contains("verify_phone_portugal()")) {
                result.setMessage("The format for the phone in Portugal is incorrect");
            } else if (e.getMessage().contains("verify_documentation_china()")) {
                result.setMessage("The format for the documentation in China is incorrect");
            } else if (e.getMessage().contains("verify_phone_china()")) {
                result.setMessage("The format for the phone in China is incorrect");
            } else if (e.getMessage().contains("Repeated documentation in another guest")) {
                result.setMessage("Repeated documentation in another guest");
                result.setColumnSQLTypes(new HashMap());
            } else if (e.getMessage().contains("insert or update on table \"guest\" violates foreign key constraint \"guest_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else if (e.getMessage().contains("Invalid email format"))
                result.setMessage("Invalid email format");
        }
        return result;
    }

    @Override
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = guestQuery(keyMap, attrList);
        if (result.getMessage().contains("The guest doesn't exist"))
            return result;
        try {
            result = this.daoHelper.update(this.guestDao, attrMap, keyMap);
            result.setMessage("Successful guest update");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (e.getMessage().contains("verify_documentation_spain()")) {
                if (e.getMessage().contains(("The spanish DNI must have 9 characters")))
                    result.setMessage("The spanish DNI must have 9 characters");
                else if (e.getMessage().contains(("The 8 first characters of a spanish DNI must be numbers")))
                    result.setMessage("The 8 first characters of a spanish DNI must be numbers");
                else if (e.getMessage().contains(("The last character of a spanish DNI must be a letter")))
                    result.setMessage("The last character of a spanish DNI must be a letter");
                else if (e.getMessage().contains(("The letter of the spanish DNI is incorrect")))
                    result.setMessage("The letter of the spanish DNI is incorrect");
                else result.setMessage("The format of the spanish DNI is incorrect");
            } else if (e.getMessage().contains("verify_phone_spain()")) {
                if (e.getMessage().contains(("The format for the spanish phone is incorrect. It must be +34 and 9 numbers")))
                    result.setMessage("The format for the spanish phone is incorrect. It must be +34 and 9 numbers");
                else result.setMessage("The format for the spanish phone is incorrect");
            } else if (e.getMessage().contains("verify_documentation_united_states()")) {
                if (e.getMessage().contains(("El format for the passport in United States is incorrect")))
                    result.setMessage("El format for the passport in United States is incorrect. It must be 9 characters");
                else result.setMessage("The format for the passport in United States is incorrect");
            } else if (e.getMessage().contains("verify_phone_united_states()")) {
                if (e.getMessage().contains(("The format for the phone in United States is incorrect")))
                    result.setMessage("The format for the phone in United States is incorrect. It must be +1 and 10 numbers");
                else result.setMessage("The format for the phone in United States is incorrect");
            } else if (e.getMessage().contains("verify_documentation_united_kingdom()")) {
                result.setMessage("The format for the passport in United Kingdom is incorrect");
            } else if (e.getMessage().contains("verify_phone_united_kingdom()")) {
                result.setMessage("The format for the phone in United Kingdom is incorrect");
            } else if (e.getMessage().contains("verify_documentation_france()")) {
                result.setMessage("The format for the card in France is incorrect");
            } else if (e.getMessage().contains("verify_phone_france()")) {
                result.setMessage("The format for the phone in France is incorrect");
            } else if (e.getMessage().contains("verify_documentation_germany()")) {
                result.setMessage("The format for the documentation in Germany is incorrect");
            } else if (e.getMessage().contains("verify_phone_germany()")) {
                result.setMessage("The format for the phone in Germany is incorrect");
            } else if (e.getMessage().contains("verify_documentation_portugal()")) {
                result.setMessage("The format for the documentation in Portugal is incorrect");
            } else if (e.getMessage().contains("verify_phone_portugal()")) {
                result.setMessage("The format for the phone in Portugal is incorrect");
            } else if (e.getMessage().contains("verify_documentation_china()")) {
                result.setMessage("The format for the documentation in China is incorrect");
            } else if (e.getMessage().contains("verify_phone_china()")) {
                result.setMessage("The format for the phone in China is incorrect");
            } else if (e.getMessage().contains("Repeated documentation in another guest")) {
                result.setMessage("Repeated documentation in another guest");
                result.setColumnSQLTypes(new HashMap());
            } else if (e.getMessage().contains("insert or update on table \"guest\" violates foreign key constraint \"guest_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else if (e.getMessage().contains("Invalid email format"))
                result.setMessage("Invalid email format");
        }
        return result;

    }

    @Override
    public EntityResult guestDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.guestDao, keyMap, attrList);
        EntityResult result = this.daoHelper.delete(this.guestDao, keyMap);

        if (query.toString().contains("id")) result.setMessage("Successful guest delete");
        else {
            result.setMessage("Guest not found");
            result.setColumnSQLTypes(new HashMap());
        }

        return result;
    }
}
