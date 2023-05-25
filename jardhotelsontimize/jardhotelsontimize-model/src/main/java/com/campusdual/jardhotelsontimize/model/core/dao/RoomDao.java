package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "RoomDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/RoomDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class RoomDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "ID";
    public static final String ATTR_NUMBER = "NUMBER";
    public static final String ATTR_CAPACITY = "CAPACITY";
    public static final String ATTR_DESCRIPTION = "DESCRIPTION";
    public static final String ATTR_HOTEL = "HOTEL";
}
