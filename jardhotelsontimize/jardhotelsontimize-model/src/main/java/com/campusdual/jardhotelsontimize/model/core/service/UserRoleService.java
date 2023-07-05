package com.campusdual.jardhotelsontimize.model.core.service;


import com.campusdual.jardhotelsontimize.api.core.service.IUserRoleService;
import com.campusdual.jardhotelsontimize.model.core.dao.UserRoleDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Lazy
@Service("UserRoleService")
public class UserRoleService implements IUserRoleService {

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private DefaultOntimizeDaoHelper daoHelper;


	public EntityResult user_roleQuery(Map<String, Object> keyMap, List<String> attrList) {

		boolean deleteId = false;
		if (!attrList.contains("id")) {
			attrList.add("id");
			deleteId = true;
		}

		EntityResult result = this.daoHelper.query(userRoleDao, keyMap, attrList);

		if(!result.toString().contains("id")){
			EntityResult error = new EntityResultMapImpl();
			error.setCode(EntityResult.OPERATION_WRONG);
			error.setMessage("User_Role not found");
			return error;
		}

		if (deleteId) {
			result.remove("id");
		}
		return result;
	}

	public EntityResult user_roleInsert(Map<String, Object> attrMap) {
		return this.daoHelper.insert(userRoleDao, attrMap);
	}

	public EntityResult user_roleUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
		return this.daoHelper.update(userRoleDao, attrMap, keyMap);
	}

	public EntityResult user_roleDelete(Map<String, Object> keyMap) {
		return this.daoHelper.delete(this.userRoleDao,keyMap);
	}

}
