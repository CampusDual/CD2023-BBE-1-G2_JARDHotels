package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IPantryService {
    EntityResult pantryQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult pantryInsert(Map<String, Object> attrMap);
    EntityResult pantryUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult pantryDelete(Map<String, Object> keyMap);
}
