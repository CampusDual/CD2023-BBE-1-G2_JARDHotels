package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IHotelService {

    EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList);
    EntityResult hotelInsert(Map<String, Object> attrMap);
    EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    EntityResult hotelDelete(Map<String, Object> keyMap);
}
