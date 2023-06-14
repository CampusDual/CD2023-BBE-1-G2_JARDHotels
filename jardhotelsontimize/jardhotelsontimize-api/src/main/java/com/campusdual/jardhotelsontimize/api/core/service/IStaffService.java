package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IStaffService {

    EntityResult staffQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult staffInsert(Map<String, Object> attrMap);
    EntityResult staffUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult staffDelete(Map<String, Object> keyMap);
}
