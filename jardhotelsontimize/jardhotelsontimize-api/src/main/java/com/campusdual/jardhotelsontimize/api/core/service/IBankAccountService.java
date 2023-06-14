package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBankAccountService {

    EntityResult bankaccountQuery(Map<String, Object> keyMap, List<String> attrList);

}
