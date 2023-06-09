package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    GuestService guestService;

    @Mock
    GuestDao guestDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class GuestServiceQuery {
        @Test
        public void guestQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> guestToQuery = new HashMap<>();
            guestToQuery.put("id", 1);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestQuery(guestToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(GuestDao.class), anyMap(), anyList());
        }

        @Test
        public void guestQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> guestToQuery = new HashMap<>();
            guestToQuery.put("id", 1);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestQuery(guestToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("The guest doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(GuestDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class GuestServiceInsert {
        @Test
        public void guestInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> guestToInsert = new HashMap<>();
            guestToInsert.put("id", 1);
            when(daoHelper.insert(any(GuestDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestInsert(guestToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(GuestDao.class), anyMap());
            assertEquals("Successful guest insertion", result.getMessage());
        }

        @Test
        public void guestInsertTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> guestToInsert = new HashMap<>();
            guestToInsert.put("id", 1);
            when(daoHelper.insert(any(GuestDao.class), anyMap())).thenThrow(new RuntimeException(""));
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestInsert(guestToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(GuestDao.class), anyMap());
            assertNotEquals("Successful guest insertion", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class GuestServiceUpdate {
        @Test
        public void guestUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> guestToUpdate = new HashMap<>();
            guestToUpdate.put("id", 1);
            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);
            when(daoHelper.update(any(GuestDao.class), anyMap(), anyMap())).thenReturn(er);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestUpdate(guestToUpdate, guestKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(GuestDao.class), anyMap(), anyMap());
            assertEquals("Successful guest update", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class GuestServiceDelete {
        @Test
        public void guestDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);
            when(daoHelper.delete(any(GuestDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestDelete(guestKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(GuestDao.class), anyMap());
            assertEquals("Successful guest delete", result.getMessage());
        }

        @Test
        public void guestDeleteTestGuestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);
            when(daoHelper.delete(any(GuestDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestDelete(guestKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(GuestDao.class), anyMap());
            assertEquals("Guest not found", result.getMessage());
        }
    }


}
