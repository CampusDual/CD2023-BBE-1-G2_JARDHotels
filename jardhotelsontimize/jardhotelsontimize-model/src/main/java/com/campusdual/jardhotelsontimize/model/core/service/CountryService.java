package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.ICountryService;
import com.campusdual.jardhotelsontimize.model.core.dao.CountryDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("CountryService")
public class CountryService implements ICountryService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private CountryDao countryDao;


    @Override
    public EntityResult countryQuery(Map<String, Object> keyMap, List<String> attrList) {

        boolean deleteId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            deleteId = true;
        }

        EntityResult result = this.daoHelper.query(this.countryDao, keyMap, attrList);

        if (result.toString().contains("id") || result.toString().contains("ID")) {
            result.setMessage("");
            if (deleteId) {
                result.remove("id");
            }

        } else {
            result.setMessage("The country doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }
}
