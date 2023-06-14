package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "BankAccountDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/BankAccountDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class BankAccountDao extends OntimizeJdbcDaoSupport {

    public static final String ATTR_ID = "id";
    public static final String ATTR_FORMAT = "format";
}
