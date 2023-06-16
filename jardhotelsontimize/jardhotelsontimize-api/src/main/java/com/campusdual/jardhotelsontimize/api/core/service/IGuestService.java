package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IGuestService {

    EntityResult guestQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult guestInsert(Map<String, Object> attrMap);
    EntityResult guestUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult guestDelete(Map<String, Object> keyMap);
}
