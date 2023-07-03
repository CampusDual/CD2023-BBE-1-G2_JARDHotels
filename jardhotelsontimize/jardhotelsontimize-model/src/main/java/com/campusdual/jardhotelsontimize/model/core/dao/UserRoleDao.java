package com.campusdual.jardhotelsontimize.model.core.dao;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;


@Repository(value = "UserRoleDao")
@Lazy
@ConfigurationFile(
	configurationFile = "dao/UserRoleDao.xml",
	configurationFilePlaceholder = "dao/placeholders.properties")
public class UserRoleDao extends OntimizeJdbcDaoSupport {
	public static final String ATTR_ID = "id";
	public static final String ATTR_ID_ROLE = "id_role";
	public static final String ATTR_USER_NAME = "user_name";
}
