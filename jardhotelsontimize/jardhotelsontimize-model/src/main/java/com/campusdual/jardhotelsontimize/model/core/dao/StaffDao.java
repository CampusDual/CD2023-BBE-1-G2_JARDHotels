package com.campusdual.jardhotelsontimize.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository(value = "StaffDao")
@Lazy
@ConfigurationFile(configurationFile = "dao/StaffDao.xml", configurationFilePlaceholder = "dao/placeholders.properties")
public class StaffDao extends OntimizeJdbcDaoSupport {

    public static final String ATTR_ID = "id";
    public static final String ATTR_BANKACCOUNT = "bankaccount";
    public static final String ATTR_BANKACCOUNTFORMAT = "bankaccountformat";
    public static final String ATTR_SALARY = "salary";
    public static final String ATTR_JOB = "job";
    public static final String ATTR_IDHOTEL = "idhotel";
}
