package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IPersonService {

    EntityResult personQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult personInsert(Map<String, Object> attrMap);
    EntityResult personUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult personDelete(Map<String, Object> keyMap);
}
