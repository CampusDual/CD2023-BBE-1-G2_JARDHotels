package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IPersonService;
import com.campusdual.jardhotelsontimize.model.core.dao.PersonDao;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Lazy
@Service("PersonService")
public class PersonService implements IPersonService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private UserService userService;

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult personQuery(Map<String, Object> keyMap, List<String> attrList) {
        EntityResult result = this.daoHelper.query(this.personDao, keyMap, attrList);
        if (result.toString().contains("id")) result.setMessage("");
        else {
            result.setMessage("The person doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult personInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();
        try {
            Map<String, Object>userMap = checkAtributesUserInsert(attrMap);
            attrMap = filterAttrMap(attrMap);

            result = this.daoHelper.insert(this.personDao, attrMap);

            Map <String, Object>keyMap = new HashMap<>();
            keyMap.put("documentation", attrMap.get("documentation"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            EntityResult personQuery = personQuery(keyMap, attrList);
            List<Object>ids = (List<Object>) personQuery.get("id");
            userMap.put("idperson", ids.get(0));
            userService.userInsert(userMap);

            result.setMessage("Successful person insertion");
        } catch (Exception e) {
            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("verify_documentation_spain()")) {
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
            } else if (e.getMessage().contains("Repeated documentation in another person")) {
                result.setMessage("Repeated documentation in another person");
                result.setColumnSQLTypes(new HashMap<>());
            } else if (e.getMessage().contains("insert or update on table \"person\" violates foreign key constraint \"person_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else if (e.getMessage().contains("insert or update on table \"person\" violates foreign key constraint \"person_phonecountry_fkey\"")) {
                    result.setMessage("The phone country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
        }
        return result;
    }

    private Map<String, Object> filterAttrMap(Map<String, Object> attrMap) {
        if(attrMap.containsKey("username")){
            attrMap.remove("username");
        }

        attrMap.remove("email");
        attrMap.remove("password");

        if(attrMap.containsKey("lastpasswordupdate")){
            attrMap.remove("lastpasswordupdate");
        }
        if(attrMap.containsKey("firstlogin")){
            attrMap.remove("firstlogin");
        }
        if(attrMap.containsKey("userbloqued")){
            attrMap.remove("userbloqued");
        }
        if(attrMap.containsKey("idperson")){
            attrMap.remove("idperson");
        }
        return attrMap;
    }

    private Map<String, Object> checkAtributesUserInsert(Map<String, Object> attrMap) {
        Map<String, Object> toret = new HashMap<>();

        if(!attrMap.containsKey("username")){
            throw new RuntimeException("Missing username attribute");
        }else{
            Map<String, Object> key = new HashMap<>();
            key.put("username", attrMap.get("username"));
            List<String> attrList = new ArrayList<>();
            attrList.add("username");
            EntityResult userQuery = userService.userQuery(key, attrList);
            if(userQuery.getCode() != EntityResult.OPERATION_WRONG){
                throw new RuntimeException("Repeated username");
            }

            toret.put("username", attrMap.get("username"));
        }

        if(!attrMap.containsKey("email")){
            throw new RuntimeException("Missing email attribute");
        }else if(!isValidEmail(attrMap.get("email").toString())){
            throw new RuntimeException("Email format not valid");
        }else{
            toret.put("email", attrMap.get("email"));
        }

        if(!attrMap.containsKey("password")){
            throw new RuntimeException("Missing password attribute");
        }else{
            toret.put("password", attrMap.get("password"));
        }
        return toret;
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult personUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = personQuery(keyMap, attrList);
        if (result.getMessage().contains("The person doesn't exist"))
            return result;
        try {
            boolean updateUser = false;

            //TODO

            result = this.daoHelper.update(this.personDao, attrMap, keyMap);
            result.setMessage("Successful person update");
        } catch (Exception e) {
            result.setCode(EntityResult.OPERATION_WRONG);
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
            } else if (e.getMessage().contains("Repeated documentation in another person")) {
                result.setMessage("Repeated documentation in another person");
                result.setColumnSQLTypes(new HashMap<>());
            } else if (e.getMessage().contains("insert or update on table \"person\" violates foreign key constraint \"person_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else if (e.getMessage().contains("insert or update on table \"person\" violates foreign key constraint \"person_phonecountry_fkey\"")) {
                result.setMessage("The phone country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
        }
        return result;

    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult personDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.personDao, keyMap, attrList);

        if (query.toString().contains("id")){
            List<Integer>ids = (List<Integer>) query.get("id");
            if(ids.get(0) == -1){
                EntityResult error = new EntityResultMapImpl();
                error.setMessage("The system´s admin cannot be deleted");
            }

            EntityResult result = this.daoHelper.delete(this.personDao, keyMap);
            result.setMessage("Successful person delete");
            return result;
        } else {
            EntityResult error = new EntityResultMapImpl();
            error.setMessage("Person not found");
            error.setCode(EntityResult.OPERATION_WRONG);
            return error;
        }
    }
}
