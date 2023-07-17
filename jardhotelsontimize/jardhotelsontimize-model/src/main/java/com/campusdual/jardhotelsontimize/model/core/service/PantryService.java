package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IPantryService;
import com.campusdual.jardhotelsontimize.model.core.dao.PantryDao;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("PantryService")
public class PantryService implements IPantryService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private PantryDao pantryDao;

    @Autowired
    private MenuService menuService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private StaffService staffService;

    @Override
    public EntityResult pantryQuery(Map<String, Object> keyMap, List<String> attrList) {
        boolean removeId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            removeId = true;
        }
        try {
            EntityResult result = this.daoHelper.query(this.pantryDao, keyMap, attrList);
            if (result.toString().contains("id")) {
                if (removeId) {
                    result.remove("id");
                    return result;
                } else {
                    return result;
                }
            } else {
                EntityResult error = new EntityResultMapImpl();
                error.setCode(EntityResult.OPERATION_WRONG);
                error.setMessage("Pantry not found");
                return error;
            }
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }

    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult pantryInsert(Map<String, Object> attrMap) {

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        try {
            Map<String, Object> key = new HashMap<>();
            List<String> attrList = new ArrayList<>();
            attrList.add("id");

            if (attrMap.containsKey("idhotel")) {
                key.put("id", attrMap.get("idhotel"));
                EntityResult hotelQuery = this.hotelService.hotelQuery(key, attrList);
                if (hotelQuery.getCode() == EntityResult.OPERATION_WRONG) {
                    return hotelQuery;
                }
            } else {
                error.setMessage("idhotel is required");
                return error;
            }

            if (attrMap.containsKey("idmenu")) {
                key = new HashMap<>();
                key.put("id", attrMap.get("idmenu"));
                EntityResult menuQuery = this.menuService.menuQuery(key, attrList);
                if (menuQuery.getCode() == EntityResult.OPERATION_WRONG) {
                    return menuQuery;
                }
            } else {
                error.setMessage("idmenu is required");
                return error;
            }

            if (attrMap.containsKey("amount")) {
                if ((Integer) attrMap.get("amount") < 0) {
                    error.setMessage("amount must be positive");
                    return error;
                }
            } else {
                error.setMessage("amount is required");
                return error;
            }

            key = new HashMap<>();
            key.put("idhotel", attrMap.get("idhotel"));
            key.put("idmenu", attrMap.get("idmenu"));
            attrList.add("amount");
            EntityResult pantryQuery = this.pantryQuery(key, attrList);
            if (pantryQuery.getCode() != EntityResult.OPERATION_WRONG) {
                List<Integer> idsPantry = (List<Integer>) pantryQuery.get("id");
                List<Integer> amounts = (List<Integer>) pantryQuery.get("amount");
                key = new HashMap<>();
                key.put("id", idsPantry.get(0));
                attrMap.put("amount", (Integer) attrMap.get("amount") + amounts.get(0));
                EntityResult pantryUpdate = pantryUpdate(attrMap, key);
                pantryUpdate.put("id", idsPantry.get(0));
                return pantryUpdate;
            }
            EntityResult result = this.daoHelper.insert(this.pantryDao, attrMap);
            result.setMessage("Successful pantry insert");
            return result;
        } catch (Exception e) {
            error.setMessage(e.getMessage());
            return error;
        }

    }

    @Override
    public EntityResult pantryUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("idhotel");
        attrList.add("idmenu");
        EntityResult pantryQuery = this.pantryQuery(keyMap, attrList);
        if (pantryQuery.getCode() == EntityResult.OPERATION_WRONG) {
            return pantryQuery;
        }

        EntityResult error = new EntityResultMapImpl();
        error.setCode(EntityResult.OPERATION_WRONG);

        try {
            Map<String, Object> key = new HashMap<>();

            if (attrMap.containsKey("idhotel")) {
                List<Integer> idsHotel = (List<Integer>) pantryQuery.get("idhotel");
                if ((Integer) attrMap.get("idhotel") != idsHotel.get(0)) {
                    error.setMessage("idhotel cannot be changed");
                    return error;
                }
                EntityResult checkPermissions = checkPermissions(attrMap.get("idhotel").toString(), "update");
                if(checkPermissions.getCode() == EntityResult.OPERATION_WRONG){
                    return checkPermissions;
                }
            }

            if (attrMap.containsKey("idmenu")) {
                List<Integer> idsMenu = (List<Integer>) pantryQuery.get("idmenu");
                if ((Integer) attrMap.get("idmenu") != idsMenu.get(0)) {
                    error.setMessage("idmenu cannot be changed");
                    return error;
                }
            }

            if (attrMap.containsKey("amount")) {
                if ((Integer) attrMap.get("amount") < 0) {
                    error.setMessage("amount must be positive");
                    return error;
                }
            }
            EntityResult result = this.daoHelper.update(this.pantryDao, attrMap, keyMap);
            result.setMessage("Successful pantry update");
            return result;
        } catch (Exception e) {
            error.setMessage(e.getMessage());
            return error;
        }
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult pantryDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("idhotel");
        EntityResult pantryQuery = this.pantryQuery(keyMap, attrList);
        if (pantryQuery.getCode() == EntityResult.OPERATION_WRONG) {
            return pantryQuery;
        }

        List<String>idsHotel = (List<String>) pantryQuery.get("idhotel");
        EntityResult checkPermissions = checkPermissions(idsHotel.get(0), "delete");
        if(checkPermissions.getCode() == EntityResult.OPERATION_WRONG){
            return checkPermissions;
        }

        try {
            EntityResult result = this.daoHelper.delete(this.pantryDao, keyMap);
            result.setMessage("Successful pantry delete");
            return result;
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }
    }

    private EntityResult checkPermissions(String idHotel, String operation){
        try {
            UserInformation userInformation = (UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Map<String, Object>key = new HashMap<>();
            key.put("username", userInformation.getUsername());
            List<String>attrList = new ArrayList<>();
            attrList.add("idperson");
            attrList.add("username");
            EntityResult userQuery = userService.userQuery(key, attrList);

            key = new HashMap<>();
            List<Object> ids=(List<Object>)userQuery.get("idperson");
            key.put("id", ids.get(0));
            attrList = new ArrayList<>();
            attrList.add("idhotel");
            attrList.add("job");
            attrList.add("id");

            EntityResult staffQuery = staffService.staffQuery(key, attrList);

            if(staffQuery.getCode() == 0){

                List<Object>idsHotel = (List<Object>) staffQuery.get("idhotel");
                List<Object>jobs = (List<Object>) staffQuery.get("job");

                if(jobs.get(0).toString().equals("10") && !(idsHotel.get(0).toString().equals(idHotel))){
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("This hotel manager can not " + operation + " the pantry from this hotel");
                    return error;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new EntityResultMapImpl();
        }
        return new EntityResultMapImpl();
    }
}
