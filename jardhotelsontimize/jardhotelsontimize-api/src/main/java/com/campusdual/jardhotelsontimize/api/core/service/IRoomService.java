package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {

    public EntityResult roomQuery(Map<String, Object> keyMap, List<String> attrList);
    public EntityResult roomInsert(Map<String, Object> attrMap);
    public EntityResult roomUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    public EntityResult roomDelete(Map<String, Object> keyMap);
}
