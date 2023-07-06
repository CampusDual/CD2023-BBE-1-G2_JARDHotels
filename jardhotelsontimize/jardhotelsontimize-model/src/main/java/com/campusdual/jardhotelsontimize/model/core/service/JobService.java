package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.campusdual.jardhotelsontimize.api.core.service.IJobService;
import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
import com.campusdual.jardhotelsontimize.model.core.dao.JobDao;
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
@Service("JobService")
public class JobService implements IJobService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private PersonService personService;

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult jobQuery(Map<String, Object> keyMap, List<String> attrList) {

        boolean deleteId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            deleteId = true;
        }

        EntityResult result = this.daoHelper.query(this.jobDao,keyMap,attrList);
        if(result.toString().contains("id")) {
            result.setMessage("");
            if (deleteId) {
                result.remove("id");
            }

        } else {
            result.setMessage("The job doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }

        return result;
    }


}
