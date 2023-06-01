package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "RoomDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/RoomDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class RoomDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "id";
    public static final String ATTR_NUMBER = "number";
    public static final String ATTR_CAPACITY = "capacity";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_HOTEL = "hotel";
    public static final String ATTR_PRICE = "price";
}
