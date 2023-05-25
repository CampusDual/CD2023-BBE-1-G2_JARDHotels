package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
        return this.daoHelper.query(this.guestDao, keyMap, attrList);
    }

    @Override
    public EntityResult guestInsert(Map<String, Object> attrMap) {
        return this.daoHelper.insert(this.guestDao, attrMap);
    }

    @Override
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        return this.daoHelper.update(this.guestDao, attrMap, keyMap);
    }

    @Override
    public EntityResult guestDelete(Map<String, Object> keyMap) {
        return this.daoHelper.delete(this.guestDao, keyMap);
    }
}
