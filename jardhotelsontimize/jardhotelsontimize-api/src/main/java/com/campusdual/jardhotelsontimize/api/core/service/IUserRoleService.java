package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IUserRoleService {
    EntityResult user_roleQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult user_roleInsert(Map<String, Object> attrMap);
    EntityResult user_roleUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult user_roleDelete(Map<String, Object> keyMap);
}
