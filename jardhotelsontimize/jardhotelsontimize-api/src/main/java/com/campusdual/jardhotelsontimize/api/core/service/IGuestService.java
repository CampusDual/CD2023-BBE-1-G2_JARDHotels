package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IGuestService {

    public EntityResult guestQuery(Map<String, Object> keyMap, List<String> attrList);
    public EntityResult guestInsert(Map<String, Object> attrMap);
    public EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    public EntityResult guestDelete(Map<String, Object> keyMap);
}
