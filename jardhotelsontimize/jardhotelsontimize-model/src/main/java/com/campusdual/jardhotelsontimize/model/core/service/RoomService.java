package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.campusdual.jardhotelsontimize.model.core.dao.RoomDao;
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
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private UserService userService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private HotelService hotelService;


    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomQuery(Map<String, Object> keyMap, List<String> attrList) {

        boolean deleteId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            deleteId = true;
        }

        EntityResult result = this.daoHelper.query(this.roomDao, keyMap, attrList);

        if (result.toString().contains("id") || result.toString().contains("ID")) {
            result.setMessage("");
            if (deleteId) {
                result.remove("id");
            }

        } else {
            result.setMessage("The room doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomInsert(Map<String, Object> attrMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result;

        if(attrMap.containsKey("hotel")){
            EntityResult checkPermissions = checkPermissions(attrMap.get("hotel").toString());
            if(checkPermissions.getCode() == EntityResult.OPERATION_WRONG){
                return checkPermissions;
            }
        }

        try {
            result = this.daoHelper.insert(this.roomDao, attrMap);
            result.setMessage("Successful room insertion");
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);

            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("room_hotel_fkey"))
                result.setMessage("Hotel not found");
            else if (e.getMessage().contains("Repeated number in hotel"))
                result.setMessage("Repeated number in hotel");
            else if (e.getMessage().contains("Number must be over zero"))
                result.setMessage("Number must be over zero");
            else if (e.getMessage().contains("Capacity must be over zero"))
                result.setMessage("Capacity must be over zero");
            else if (e.getMessage().contains("The price must be greater than 0"))
                result.setMessage("The price must be greater than 0");
            else result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult result = roomQuery(keyMap, attrList);
        if (result.getMessage().contains("The room doesn't exist")) {
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        EntityResult checkPermissions = checkPermissions(Integer.parseInt(keyMap.get("id").toString()), "update");
        if(checkPermissions.getCode() == EntityResult.OPERATION_WRONG){
            return checkPermissions;
        }

        try {
            result = this.daoHelper.update(this.roomDao, attrMap, keyMap);
            result.setMessage("Successful room update");
        } catch (Exception e) {
            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("Change the hotel of a room is not allowed"))
                result.setMessage("Change the hotel of a room is not allowed");
            else if (e.getMessage().contains("Number must be over zero"))
                result.setMessage("Number must be over zero");
            else if (e.getMessage().contains("Repeated number in hotel"))
                result.setMessage("Repeated number in hotel");
            else if (e.getMessage().contains("Capacity must be over zero"))
                result.setMessage("Capacity must be over zero");
            else if (e.getMessage().contains("The price must be greater than 0"))
                result.setMessage("The price must be greater than 0");
            else result.setMessage(e.getMessage());

            result.setColumnSQLTypes(new HashMap<>());
        }

        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roomDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");

        EntityResult query = this.daoHelper.query(this.roomDao, keyMap, attrList);

        if (query.toString().contains("id")){
            EntityResult checkPermissions = checkPermissions(Integer.parseInt(keyMap.get("id").toString()), "delete");
            if(checkPermissions.getCode() == EntityResult.OPERATION_WRONG){
                return checkPermissions;
            }
            EntityResult result = this.daoHelper.delete(this.roomDao, keyMap);
            result.setMessage("Successful room delete");
            return result;
        } else {
            EntityResult error = new EntityResultMapImpl();
            error.setMessage("Room not found");
            error.setCode(EntityResult.OPERATION_WRONG);
            return error;
        }
    }

    private EntityResult checkPermissions(int idRoom, String operation){
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
                key = new HashMap<>();
                key.put("id", idRoom);
                attrList = new ArrayList<>();
                attrList.add("id");
                attrList.add("hotel");
                EntityResult roomQuery = roomQuery(key, attrList);
                List<Object> hotelIds = (List<Object>) roomQuery.get("hotel");

                List<Object>idsHotel = (List<Object>) staffQuery.get("idhotel");
                List<Object>jobs = (List<Object>) staffQuery.get("job");

                if(jobs.get(0).toString().equals("10") && !(idsHotel.get(0).toString().equals(hotelIds.get(0).toString()))){
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("This hotel manager can not " + operation + " rooms in this hotel");
                    return error;
                }
            }
        }catch (Exception e){
            return new EntityResultMapImpl();
        }
        return new EntityResultMapImpl();
    }

    private EntityResult checkPermissions(String idHotel){
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
                    error.setMessage("This hotel manager can not insert rooms in this hotel");
                    return error;
                }
            }
        }catch (Exception e){
            return new EntityResultMapImpl();
        }
        return new EntityResultMapImpl();
    }


}
