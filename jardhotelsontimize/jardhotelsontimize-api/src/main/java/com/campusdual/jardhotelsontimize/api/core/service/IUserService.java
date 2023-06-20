package com.campusdual.jardhotelsontimize.api.core.service;


import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.dto.EntityResult;


public interface IUserService {

	EntityResult userQuery(Map<String, Object> keyMap, List<String> attrList);
	EntityResult userInsert(Map<String, Object> attrMap);
	EntityResult userUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
	EntityResult userDelete(Map<String, Object> keyMap);

}
