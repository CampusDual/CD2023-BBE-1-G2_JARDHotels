package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IStaffService;
import com.campusdual.jardhotelsontimize.model.core.dao.StaffDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.Keymap;
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
    private GuestService guestService;

    @Autowired
    private JobService jobService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult staffQuery(Map<String, Object> keyMap, List<String> attrList) {

        boolean deleteId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            deleteId = true;
        }

        List<String> attrList2 = new ArrayList<>();
        attrList2.add("id");
        if (attrList.contains("bankaccount")) {
            attrList2.add("bankaccount");
            attrList.remove("bankaccount");
        }
        if (attrList.contains("bankaccountformat")) {
            attrList2.add("bankaccountformat");
            attrList.remove("bankaccountformat");
        }
        if (attrList.contains("salary")) {
            attrList2.add("salary");
            attrList.remove("salary");
        }
        if (attrList.contains("job")) {
            attrList2.add("job");
            attrList.remove("job");
        }
        if (attrList.contains("idhotel")) {
            attrList2.add("idhotel");
            attrList.remove("idhotel");
        }
        EntityResult erStaff = this.daoHelper.query(this.staffDao, keyMap, attrList2);
        if (!erStaff.toString().contains("id")) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage("The staff member doesn't exist");

            return error;
        }

        if (!attrList.contains("id")) attrList.add("id");
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
                for (int j = 0; j < listIds.size(); j++) {
                    if (listIds.get(j) == ((List<Integer>) erPerson.get("id")).get(i)) {
                        position = j;
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

        if (deleteId) {
            er.remove("id");
        }
        return er;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult staffInsert(Map<String, Object> attrMap) {
        if (attrMap.get("bankaccount") == null || attrMap.get("bankaccountformat") == null || attrMap.get("salary") == null || attrMap.get("job") == null || attrMap.get("idhotel") == null) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage("All attributes must be filled");
            return error;
        }
        try {
            EntityResult checkPermissions = checkPermissionsInsert(attrMap, "insert");
            if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                return checkPermissions;
            }
        } catch (Exception e) {
        }
        Map<String, Object> copy = new HashMap<>(attrMap);
        copy.remove("bankaccount");
        copy.remove("bankaccountformat");
        copy.remove("salary");
        copy.remove("job");
        copy.remove("idhotel");
        if (attrMap.get("id") != null) {

            try {
                int idAdmin = (int) attrMap.get("id");
                if (idAdmin == -1) {
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("The given id is not valid");
                    return error;
                }
            } catch (Exception e) {}

            Map<String, Object> map = new HashMap<>();
            map.put("id", attrMap.get("id"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            EntityResult resultId = personService.personQuery(map, attrList);
            if (resultId.getCode() == 0) {
                EntityResult queryStaff = staffQuery(map, attrList);
                if (queryStaff.getCode() == 1) {
                    try {
                        Map<String, Object> keyMap = new HashMap<>();
                        keyMap.put("idperson", attrMap.get("id"));

                        List<String> attrListQuery = new ArrayList<>();
                        attrListQuery.add("username");

                        EntityResult userQuery = this.userService.userQuery(keyMap, attrListQuery);

                        if (userQuery.getCode() != EntityResult.OPERATION_WRONG) {
                            keyMap = new HashMap<>();
                            keyMap.put("user_name", userQuery.get("username"));
                            int job = (int) attrMap.get("job");
                            switch (job) {
                                case 3:
                                    keyMap.put("id_role", 2);
                                    userRoleService.user_roleInsert(keyMap);
                                    break;
                                case 10:
                                    keyMap.put("id_role", 3);
                                    userRoleService.user_roleInsert(keyMap);
                                    break;
                            }
                        }

                        map.put("bankaccount", attrMap.get("bankaccount"));
                        map.put("bankaccountformat", attrMap.get("bankaccountformat"));
                        map.put("salary", attrMap.get("salary"));
                        map.put("job", attrMap.get("job"));
                        map.put("idhotel", attrMap.get("idhotel"));
                        checkAttributesStaff(map);
                        EntityResult result = this.daoHelper.insert(this.staffDao, map);
                        result.setMessage("Successful staff insert");
                        return result;
                    } catch (Exception e) {
                        EntityResult error = new EntityResultMapImpl();
                        error.setCode(EntityResult.OPERATION_WRONG);
                        error.setMessage(e.getMessage());
                        return error;
                    }

                } else {
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("Repeated staff member");
                    return error;
                }

            } else {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage("Person not found");
                return error;
            }
        }
        try {
            checkAttributesStaff(attrMap);
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }
        EntityResult result = personService.personInsert(copy);
        if (result.getCode() == 0) {

            String username = attrMap.get("username").toString();

            Map<String, Object> map = new HashMap<>();
            map.put("documentation", attrMap.get("documentation"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            EntityResult resultId = personService.personQuery(map, attrList);
            List<Integer> ids = (List<Integer>) resultId.get("id");
            int id = ids.get(0);
            map = new HashMap<>();
            map.put("id", id);
            map.put("bankaccount", attrMap.get("bankaccount"));
            map.put("bankaccountformat", attrMap.get("bankaccountformat"));
            map.put("salary", attrMap.get("salary"));
            map.put("job", attrMap.get("job"));
            map.put("idhotel", attrMap.get("idhotel"));
            this.daoHelper.insert(this.staffDao, map);
            result.setMessage("Successful staff member insertion");

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("user_name", username);
            int job = (int) attrMap.get("job");
            switch (job) {
                case 3:
                    keyMap.put("id_role", 2);
                    userRoleService.user_roleInsert(keyMap);
                    break;
                case 10:
                    keyMap.put("id_role", 3);
                    userRoleService.user_roleInsert(keyMap);
                    break;
            }
        }
        return result;
    }

    private EntityResult checkPermissionsInsert(Map<String, Object> attrMap, String operation) {
        UserInformation userInformation = (UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("username", userInformation.getUsername());
        List<String> attrList = new ArrayList<>();
        attrList.add("idperson");
        attrList.add("username");
        EntityResult userQuery = userService.userQuery(keyMap, attrList);
        keyMap = new HashMap<>();
        List<Integer>ids = (List<Integer>) userQuery.get("idperson");
        keyMap.put("id", ids.get(0));
        attrList = new ArrayList<>();
        attrList.add("idhotel");
        attrList.add("job");
        attrList.add("id");
        EntityResult staffQuery = staffQuery(keyMap, attrList);

        if (staffQuery.getCode() != EntityResult.OPERATION_WRONG ) {

            List<Object> jobs = (List<Object>) staffQuery.get("job");
            List<Integer> idsStaff = (List<Integer>) staffQuery.get("idhotel");

            int idhotel = idsStaff.get(0);
            int idhotel2 = (int) attrMap.get("idhotel");

            if (idhotel != idhotel2 && jobs.get(0).toString().equals("10")) {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage("This hotel manager can only " + operation + " staff in the hotel " + idhotel);
                return error;
            }
        }
        return new EntityResultMapImpl();
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult staffUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult erStaff = staffQuery(keyMap, attrList);

        if (erStaff.getCode() != EntityResult.OPERATION_WRONG) {
            Map<String, Object> attrMap2 = new HashMap<>();
            if (attrMap.containsKey("bankaccount")) {
                attrMap2.put("bankaccount", attrMap.get("bankaccount"));
                attrMap.remove("bankaccount");
            }
            if (attrMap.containsKey("bankaccountformat")) {
                attrMap2.put("bankaccountformat", attrMap.get("bankaccountformat"));
                attrMap.remove("bankaccountformat");
            }
            if (attrMap.containsKey("salary")) {
                attrMap2.put("salary", attrMap.get("salary"));
                attrMap.remove("salary");
            }
            if (attrMap.containsKey("job")) {
                try {
                    EntityResult checkPermissions = checkPermissionsInsert(attrMap, "update");
                    if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                        return checkPermissions;
                    }
                } catch (Exception e) {
                }
                attrMap2.put("job", attrMap.get("job"));
                attrMap.remove("job");
            }
            try {
                EntityResult checkPermissions = checkPermissionsUpdate((int) keyMap.get("id"));
                if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                    return checkPermissions;
                }
            } catch (Exception e) {
            }
            try {
                checkAttributesStaff(attrMap2, keyMap);
            } catch (Exception e) {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage(e.getMessage());
                return error;
            }

            EntityResult erPerson = personService.personUpdate(attrMap, keyMap);
            if (erPerson.getCode() == 0) {
                try {
                    List<String> attrList2 = new ArrayList<>();
                    attrList2.add("id");
                    attrList2.add("job");
                    EntityResult staffQuery = staffQuery(keyMap, attrList2);
                    List<Integer> jobs = (List<Integer>) staffQuery.get("job");
                    int oldJob = jobs.get(0);

                    this.daoHelper.update(this.staffDao, attrMap2, keyMap);

                    if (attrMap2.containsKey("job")) {

                        int newJob = (int) attrMap2.get("job");
                        if (oldJob != newJob) {

                            Map<String, Object> key = new HashMap<>();
                            key.put("idperson", keyMap.get("id"));
                            List<String> attrList3 = new ArrayList<>();
                            attrList3.add("username");

                            EntityResult userQuery = userService.userQuery(key, attrList3);
                            List<String> usernames = (List<String>) userQuery.get("username");

                            key = new HashMap<>();
                            attrList3 = new ArrayList<>();
                            attrList3.add("id");

                            List<Integer> ids;

                            switch (oldJob) {
                                case 3:
                                    key.put("id_role", 2);
                                    EntityResult userRoleQuery = userRoleService.user_roleQuery(key, attrList3);
                                    ids = (List<Integer>) userRoleQuery.get("id");
                                    key = new HashMap<>();
                                    key.put("id", ids.get(0));
                                    userRoleService.user_roleDelete(key);
                                    break;
                                case 10:
                                    key.put("id_role", 3);
                                    EntityResult userRoleQuery2 = userRoleService.user_roleQuery(key, attrList3);
                                    ids = (List<Integer>) userRoleQuery2.get("id");
                                    key = new HashMap<>();
                                    key.put("id", ids.get(0));
                                    userRoleService.user_roleDelete(key);
                                    break;
                            }

                            key = new HashMap<>();
                            key.put("user_name", usernames.get(0));

                            switch (newJob) {
                                case 3:
                                    key.put("id_role", 2);
                                    userRoleService.user_roleInsert(key);
                                    break;
                                case 10:
                                    key.put("id_role", 3);
                                    userRoleService.user_roleInsert(key);
                                    break;
                            }
                        }
                    }

                } catch (Exception e) {
                    EntityResult error = new EntityResultMapImpl();
                    error.setMessage(e.getMessage());
                    error.setCode(EntityResult.OPERATION_WRONG);
                    return error;
                }
                erPerson.setMessage("Successful staff update");
            } else {
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

    private EntityResult checkPermissionsUpdate(int idStaff) {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("id", idStaff);
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("idhotel");
        EntityResult staffQuery = staffQuery(keyMap, attrList);
        keyMap = new HashMap<>();
        List<Integer> idshotel = (List<Integer>) staffQuery.get("idhotel");
        keyMap.put("idhotel", idshotel.get(0));
        EntityResult er = checkPermissionsInsert(keyMap, "");
        if (er.getCode() == EntityResult.OPERATION_WRONG) {
            er.setMessage("This hotel manager can't update staff from other hotels");
            return er;
        }
        return new EntityResultMapImpl();
    }

    private void checkAttributesStaff(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("bankaccountformat");
        attrList.add("bankaccount");
        attrList.add("salary");
        attrList.add("job");
        EntityResult erStaff = staffQuery(keyMap, attrList);

        if (!attrMap.containsKey("bankaccountformat")) {
            attrMap.put("bankaccountformat", erStaff.get("bankaccountformat"));
        }

        if (!attrMap.containsKey("bankaccount")) {
            attrMap.put("bankaccount", erStaff.get("bankaccount"));
        }

        if (!attrMap.containsKey("salary")) {
            attrMap.put("salary", erStaff.get("salary"));
        }

        if (!attrMap.containsKey("job")) {
            attrMap.put("job", erStaff.get("job"));
        }

        checkAttributesStaff(attrMap);
    }

    private void checkAttributesStaff(Map<String, Object> attrMap) {

        if (attrMap.containsKey("bankaccountformat")) {
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            Map<String, Object> key = new HashMap<>();
            key.put("id", attrMap.get("bankaccountformat"));

            EntityResult bankQuery = bankAccountService.bankaccountQuery(key, attrList);
            if (bankQuery.getCode() == EntityResult.OPERATION_WRONG) {
                throw new RuntimeException("Bank Account Format Not Found");
            }
        }

        if (attrMap.containsKey("bankaccount")) {
            Integer format = Integer.parseInt(attrMap.get("bankaccountformat").toString());
            switch (format) {
                case 1:
                    if (!attrMap.get("bankaccount").toString().matches("^[A-Z]{2}\\d{2}[A-Z0-9]{4}\\d{7}([A-Z0-9]?){0,16}$")) {
                        throw new RuntimeException("Bank Account Format IBAN Not Valid");
                    }
                    break;
                case 2:
                    if (!attrMap.get("bankaccount").toString().matches("\\d{4}\\d{4}\\d{2}\\d{10}")) {
                        throw new RuntimeException("Bank Account Format CCC Not Valid");
                    }
                    break;
                case 3:
                    if (!attrMap.get("bankaccount").toString().matches("\\d{6}")) {
                        throw new RuntimeException("Bank Account Format Sort Code Not Valid");
                    }
                    break;
                case 4:
                    if (!attrMap.get("bankaccount").toString().matches("\\d{9}")) {
                        throw new RuntimeException("Bank Account Format ABA Not Valid");
                    }
                    break;
            }
        }

        if (attrMap.containsKey("salary")) {
            Double salary = Double.parseDouble(attrMap.get("salary").toString());
            if (salary <= 0) {
                throw new RuntimeException("Salary must be greater than 0");
            }
        }
        if (attrMap.containsKey("job")) {
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            Map<String, Object> key = new HashMap<>();
            key.put("id", attrMap.get("job"));

            EntityResult jobQuery = jobService.jobQuery(key, attrList);
            if (jobQuery.getCode() == EntityResult.OPERATION_WRONG) {
                throw new RuntimeException("Job not found");
            }
        }

        if (attrMap.containsKey("idhotel")) {
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            Map<String, Object> key = new HashMap<>();
            key.put("id", attrMap.get("idhotel"));

            EntityResult hotelQuery = hotelService.hotelQuery(key, attrList);
            if (hotelQuery.getCode() == EntityResult.OPERATION_WRONG) {
                throw new RuntimeException("Hotel Not Found");
            }
        }

    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult staffDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("idhotel");
        attrList.add("job");
        EntityResult erStaff = this.daoHelper.query(this.staffDao, keyMap, attrList);
        if (erStaff.toString().contains("id")) {
            Map<String, Object> key = new HashMap<>();
            List<Integer>idsHotel = (List<Integer>) erStaff.get("idhotel");
            key.put("idhotel", idsHotel.get(0));
            try {
                EntityResult checkPermissions = checkPermissionsInsert(key, "delete");
                if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                    return checkPermissions;
                }
            } catch (Exception e) {
            }
            attrList.remove("idhotel");
            attrList.remove("job");
            EntityResult erGuest = guestService.guestQuery(keyMap, attrList);
            if (erGuest.toString().contains("id")) {

                List<Integer> jobs = (List<Integer>) erStaff.get("job");
                int job = jobs.get(0);
                List<Integer> ids = (List<Integer>) erStaff.get("id");

                key = new HashMap<>();
                key.put("idperson", ids.get(0));
                List<String> attrList2 = new ArrayList<>();
                attrList2.add("username");
                EntityResult userQuery = userService.userQuery(key, attrList2);

                List<String> usernames = (List<String>) userQuery.get("username");
                key = new HashMap<>();
                key.put("user_name", usernames.get(0));

                attrList2 = new ArrayList<>();
                attrList2.add("id");

                switch (job) {
                    case 3:
                        key.put("id_role", 2);
                        EntityResult userRoleQuery = userRoleService.user_roleQuery(key, attrList2);
                        ids = (List<Integer>) userRoleQuery.get("id");
                        key = new HashMap<>();
                        key.put("id", ids.get(0));
                        userRoleService.user_roleDelete(key);
                        break;
                    case 10:
                        key.put("id_role", 3);
                        EntityResult userRoleQuery2 = userRoleService.user_roleQuery(key, attrList2);
                        ids = (List<Integer>) userRoleQuery2.get("id");
                        key = new HashMap<>();
                        key.put("id", ids.get(0));
                        userRoleService.user_roleDelete(key);
                        break;
                }

                EntityResult deleteStaff = this.daoHelper.delete(this.staffDao, keyMap);
                deleteStaff.setMessage("Successful staff delete");
                return deleteStaff;
            }
            EntityResult erPerson = personService.personDelete(keyMap);
            if (erPerson.getCode() == 0) {
                erPerson.setMessage("Successful staff delete");
            }
            return erPerson;
        }

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);
        error.setMessage("Staff not found");
        return error;
    }
}
