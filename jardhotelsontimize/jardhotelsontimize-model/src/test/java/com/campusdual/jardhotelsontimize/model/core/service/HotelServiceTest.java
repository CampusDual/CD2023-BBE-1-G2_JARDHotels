package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    HotelService hotelService;

    @Mock
    HotelDao hotelDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class HotelServiceQuery {
        @Test
        public void hotelQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> hotelToQuery = new HashMap<>();
            hotelToQuery.put("id", 1);
            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = hotelService.hotelQuery(hotelToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("The hotel has been found", result.getMessage());
            verify(daoHelper, times(1)).query(any(HotelDao.class), anyMap(), anyList());
        }

        @Test
        public void hotelQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToQuery = new HashMap<>();
            hotelToQuery.put("id", 1);
            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = hotelService.hotelQuery(hotelToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("The hotel doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(HotelDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class HotelServiceInsert {
        @Test
        public void hotelInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();
            hotelToInsert.put("name", "Hotel 1");
            hotelToInsert.put("address", "Address 1");
            hotelToInsert.put("stars", 1);
            hotelToInsert.put("country", 1);
            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);
            EntityResult result = hotelService.hotelInsert(hotelToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(HotelDao.class), anyMap());
            assertEquals("Successful hotel insertion", result.getMessage());
        }

        @Test
        public void hotelInsertTestException() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();
            hotelToInsert.put("name", "Hotel 1");
            hotelToInsert.put("address", "Address 1");
            hotelToInsert.put("stars", 1);
            hotelToInsert.put("country", 1);
            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenThrow(new RuntimeException());
            EntityResult result = hotelService.hotelInsert(hotelToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(HotelDao.class), anyMap());
            assertEquals("The country doesn't exist", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class HotelServiceUpdate {
        @Test
        public void hotelUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToUpdate = new HashMap<>();
            hotelToUpdate.put("name", "Hotel 1");
            hotelToUpdate.put("address", "Address 1");
            hotelToUpdate.put("stars", 1);
            hotelToUpdate.put("country", 1);
            Map<String, Object> hotelKey = new HashMap<>();
            hotelKey.put("id", 1);
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenReturn(er);
            EntityResult result = hotelService.hotelUpdate(hotelToUpdate, hotelKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(HotelDao.class), anyMap(), anyMap());
            assertEquals("Successful hotel update", result.getMessage());
        }

        @Test
        public void hotelUpdateTestHotelNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(2);
            Map<String, Object> hotelToUpdate = new HashMap<>();
            hotelToUpdate.put("name", "Hotel 1");
            hotelToUpdate.put("address", "Address 1");
            hotelToUpdate.put("stars", 1);
            hotelToUpdate.put("country", 1);
            Map<String, Object> hotelKey = new HashMap<>();
            hotelKey.put("id", 1);
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenReturn(er);
            EntityResult result = hotelService.hotelUpdate(hotelToUpdate, hotelKey);
            assertEquals(2, result.getCode());
            assertEquals("Hotel not found", result.getMessage());
        }

        @Test
        public void hotelUpdateTestCountryNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToUpdate = new HashMap<>();
            hotelToUpdate.put("name", "Hotel 1");
            hotelToUpdate.put("address", "Address 1");
            hotelToUpdate.put("stars", 1);
            hotelToUpdate.put("country", 1);
            Map<String, Object> hotelKey = new HashMap<>();
            hotelKey.put("id", 1);
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenThrow(new RuntimeException());
            EntityResult result = hotelService.hotelUpdate(hotelToUpdate, hotelKey);
            assertEquals(0, result.getCode());
            assertEquals("The country doesn't exist", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class HotelServiceDelete {
        @Test
        public void hotelDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> hotelKey = new HashMap<>();
            hotelKey.put("id", 1);
            when(daoHelper.delete(any(HotelDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = hotelService.hotelDelete(hotelKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(HotelDao.class), anyMap());
            assertEquals("Successful hotel delete", result.getMessage());
        }

        @Test
        public void hotelDeleteTestHotelNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            Map<String, Object> hotelKey = new HashMap<>();
            hotelKey.put("id", 1);
            when(daoHelper.delete(any(HotelDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = hotelService.hotelDelete(hotelKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(HotelDao.class), anyMap());
            assertEquals("Hotel not found", result.getMessage());
        }
    }


}
