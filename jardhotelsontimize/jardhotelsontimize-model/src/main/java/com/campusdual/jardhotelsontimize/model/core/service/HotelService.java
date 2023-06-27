package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ontimize.jee.common.services.user.UserInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private HotelDao hotelDao;

    @Autowired
    private UserService userService;

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList) {

        EntityResult result = this.daoHelper.query(this.hotelDao,keyMap,attrList);
        if(result.toString().contains("id")) {
            result.setMessage("");

        } else {
            result.setMessage("The hotel doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }

        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();

        if(attrMap.containsKey("latitude")){
            Double l = Double.parseDouble(attrMap.get("latitude").toString());

            if(l > 90 || l < -90){
                result.setMessage("Latitude range must be between -90 and +90");
                result.setCode(EntityResult.OPERATION_WRONG);
                return result;
            }
        }

        if(attrMap.containsKey("longitude")){
            Double l = Double.parseDouble(attrMap.get("longitude").toString());

            if(l > 180 || l < -180){
                result.setMessage("Longitude range must be between -180 and +180");
                result.setCode(EntityResult.OPERATION_WRONG);
                return result;
            }
        }

        try{
            result = this.daoHelper.insert(this.hotelDao,attrMap);
            result.setMessage("Successful hotel insertion");
        }catch (Exception e){
            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("Stars must be between one and five")) {
                result.setMessage("Stars must be between one and five");
            } else if (e.getMessage().contains("\"hotel\" violates foreign key constraint \"hotel_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
       try {
           UserInformation userInformation = (UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
           EntityResult userQuery = userService.userQuery();
           System.err.println(userInformation.getUsername());
       }catch (Exception e){

       }
        List<String>attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult hotelQuery = hotelQuery(keyMap, attrList);

        if(hotelQuery.getCode() == EntityResult.OPERATION_WRONG){
            hotelQuery.setMessage("Hotel not found");
            return hotelQuery;
        }

        EntityResult result = new EntityResultMapImpl();

        if(attrMap.containsKey("latitude")){
            List<Double>latitudeList = (List<Double>) attrMap.get("latitude");
            for(Double l : latitudeList){
                if(l > 90 || l < -90){
                    result.setMessage("Latitude range must be between -90 and +90");
                    result.setCode(EntityResult.OPERATION_WRONG);
                    return result;
                }
            }
        }

        if(attrMap.containsKey("longitude")){
            List<Double>longitudeList = (List<Double>) attrMap.get("longitude");
            for(Double l : longitudeList){
                if(l > 180 || l < -180){
                    result.setMessage("Longitude range must be between -180 and +180");
                    result.setCode(EntityResult.OPERATION_WRONG);
                    return result;
                }
            }
        }

        try {
            result = this.daoHelper.update(this.hotelDao,attrMap,keyMap);
            if (result.getCode() == 0)
                result.setMessage("Successful hotel update");
            if (result.getCode() == 2) {
                result.setMessage("Hotel not found");
                result.setCode(EntityResult.OPERATION_WRONG);
            }
        }catch (Exception e){
            if (e.getMessage().contains("Stars must be between one and five")) {
                result.setMessage("Stars must be between one and five");
            } else if (e.getMessage().contains("\"hotel\" violates foreign key constraint \"hotel_country_fkey\"")) {
                result.setMessage("The country doesn't exist");
            } else {
                result.setMessage(e.getMessage());
            }
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelDelete(Map<String, Object> keyMap) {

        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        attrList.add("name");
        EntityResult query = this.daoHelper.query(this.hotelDao,keyMap,attrList);

        EntityResult result = this.daoHelper.delete(this.hotelDao,keyMap);

        if (query.toString().contains("id"))
            result.setMessage("Successful hotel delete");
        else {
            result.setMessage("Hotel not found");
            result.setCode(EntityResult.OPERATION_WRONG);
        }
        return result;
    }
}
