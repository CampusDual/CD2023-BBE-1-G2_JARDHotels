package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "PantryDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/PantryDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class PantryDao extends OntimizeJdbcDaoSupport {
    public static final String ATTR_ID = "id";
    public static final String ATTR_IDMENU = "idmenu";
    public static final String ATTR_IDHOTEL = "idhotel";
    public static final String ATTR_AMOUNT = "amount";
}
