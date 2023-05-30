package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "GuestDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/GuestDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class GuestDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_SURNAME = "surname";
    public static final String ATTR_PHONE = "phone";
    public static final String ATTR_EMAIL = "email";
    public static final String ATTR_DOCUMENTATION = "documentation";
    public static final String ATTR_COUNTRY = "country";
}
