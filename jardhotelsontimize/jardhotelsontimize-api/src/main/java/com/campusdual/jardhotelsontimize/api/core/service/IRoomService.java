package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {

    EntityResult roomQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult roomInsert(Map<String, Object> attrMap);
    EntityResult roomUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult roomDelete(Map<String, Object> keyMap);
}
