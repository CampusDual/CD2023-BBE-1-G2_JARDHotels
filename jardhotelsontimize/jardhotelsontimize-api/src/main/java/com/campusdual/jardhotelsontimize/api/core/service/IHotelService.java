package com.campusdual.jardhotelsontimize.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IHotelService {

    public EntityResult hotelQuery(Map<String, Object> keyMap, List<String> attrList);
    public EntityResult hotelInsert(Map<String, Object> attrMap);
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    public EntityResult hotelDelete(Map<String, Object> keyMap);
}
