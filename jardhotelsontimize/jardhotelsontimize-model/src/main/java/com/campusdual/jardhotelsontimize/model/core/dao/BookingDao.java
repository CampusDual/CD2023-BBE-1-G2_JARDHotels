package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "BookingDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/BookingDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class BookingDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "id";
    public static final String ATTR_ROOM = "room";
    public static final String ATTR_GUEST = "guest";
    public static final String ATTR_CHECKINDATE = "checkindate";
    public static final String ATTR_CHECKOUTDATE = "checkoutdate";
    public static final String ATTR_TOTALPRICE = "totalprice";
}
