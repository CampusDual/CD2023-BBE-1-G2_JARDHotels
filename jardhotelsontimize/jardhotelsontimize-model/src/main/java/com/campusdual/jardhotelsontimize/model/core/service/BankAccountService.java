package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IBankAccountService;
import com.campusdual.jardhotelsontimize.api.core.service.IJobService;
import com.campusdual.jardhotelsontimize.model.core.dao.BankAccountDao;
import com.campusdual.jardhotelsontimize.model.core.dao.JobDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("BankAccountService")
public class BankAccountService implements IBankAccountService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private BankAccountDao bankAccountDao;

    @Autowired
    private PersonService personService;

    @Override
    public EntityResult bankaccountQuery(Map<String, Object> keyMap, List<String> attrList) {

        EntityResult result = this.daoHelper.query(this.bankAccountDao,keyMap,attrList);
        if(result.toString().contains("id")) {
            result.setMessage("");

        } else {
            result.setMessage("The bank account format doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }

        return result;
    }


}
