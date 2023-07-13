package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IMenuService;
import com.campusdual.jardhotelsontimize.model.core.dao.MenuDao;
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

@Lazy
@Service("MenuService")
public class MenuService implements IMenuService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private MenuDao menuDao;

    @Override
    public EntityResult menuQuery(Map<String, Object> keyMap, List<String> attrList) {
        boolean removeId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            removeId = true;
        }
        try {
            EntityResult result = this.daoHelper.query(this.menuDao, keyMap, attrList);
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
                error.setMessage("Menu not found");
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
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult menuInsert(Map<String, Object> attrMap) {

        try {
            if (attrMap.containsKey("name")) {
                attrMap.put("name", processString(attrMap.get("name").toString()));
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("name", attrMap.get("name"));
                List<String> attrList = new ArrayList<>();
                attrList.add("id");
                EntityResult menuQuery = this.menuQuery(keyMap, attrList);
                if (menuQuery.getCode() != EntityResult.OPERATION_WRONG) {
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("Menu already exists");
                    return error;
                }
            }
            EntityResult result = this.daoHelper.insert(this.menuDao, attrMap);
            result.setMessage("Successfully menu insert");
            return result;
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }

    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult menuUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");

        EntityResult menuQuery = this.menuQuery(keyMap, attrList);
        if (menuQuery.getCode() == EntityResult.OPERATION_WRONG) {
            return menuQuery;
        }

        try {
            if (attrMap.containsKey("name")) {
                attrMap.put("name", processString(attrMap.get("name").toString()));
                Map<String, Object> key = new HashMap<>();
                key.put("name", attrMap.get("name"));
                attrList = new ArrayList<>();
                attrList.add("id");
                menuQuery = this.menuQuery(key, attrList);
                if (menuQuery.getCode() != EntityResult.OPERATION_WRONG) {
                    List<Integer> idList = (List<Integer>) menuQuery.get("id");
                    if (idList.get(0) != (Integer) keyMap.get("id")) {
                        EntityResult error = new EntityResultMapImpl();
                        error.setCode(EntityResult.OPERATION_WRONG);
                        error.setMessage("Menu already exists");
                        return error;
                    }
                }
            }
            EntityResult result = this.daoHelper.update(this.menuDao, attrMap, keyMap);
            result.setMessage("Successfully menu update");
            return result;
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult menuDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("id");
        EntityResult menuQuery = this.menuQuery(keyMap, attrList);
        if (menuQuery.getCode() == EntityResult.OPERATION_WRONG) {
            return menuQuery;
        }
        try {
            EntityResult result = this.daoHelper.delete(this.menuDao, keyMap);
            result.setMessage("Successfully menu delete");
            return result;
        } catch (Exception e) {
            EntityResult error = new EntityResultMapImpl();
            error.setCode(EntityResult.OPERATION_WRONG);
            error.setMessage(e.getMessage());
            return error;
        }
    }

    public static String processString(String input) {
        String processedString = input.replaceAll("\\s+", " ");

        processedString = processedString.toLowerCase();

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : processedString.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
            }

            result.append(c);
        }

        return result.toString();
    }
}


