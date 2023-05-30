package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "HotelDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/HotelDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class HotelDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_STARS = "stars";
    public static final String ATTR_ADDRESS = "address";
}
