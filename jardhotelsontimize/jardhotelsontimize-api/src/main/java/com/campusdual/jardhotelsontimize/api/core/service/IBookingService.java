package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBookingService {

    public EntityResult bookingQuery(Map<String, Object> keyMap, List<String> attrList);

    public EntityResult bookingInsert(Map<String, Object> attrMap);

    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);

    public EntityResult bookingDelete(Map<String, Object> keyMap);
}
