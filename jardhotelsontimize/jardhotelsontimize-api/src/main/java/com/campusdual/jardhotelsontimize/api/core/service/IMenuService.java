package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IMenuService {
    EntityResult menuQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult menuInsert(Map<String, Object> attrMap);
    EntityResult menuUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult menuDelete(Map<String, Object> keyMap);
}
