package com.campusdual.jardhotelsontimize.api.core.service;


import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.dto.EntityResult;


public interface IUserService {

	EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList);
	EntityResult userInsert(Map<?, ?> attrMap);
	EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);
	EntityResult userDelete(Map<?, ?> keyMap);

}
