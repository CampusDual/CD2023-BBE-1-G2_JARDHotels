package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.RoomDao;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    RoomService roomService;

    @Mock
    RoomDao roomDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoomServiceQuery {
        @Test
        void roomQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> roomToQuery = new HashMap<>();
            roomToQuery.put("id", 1);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomQuery(roomToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(RoomDao.class), anyMap(), anyList());
        }

        @Test
        void roomQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> roomToQuery = new HashMap<>();
            roomToQuery.put("id", 1);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomQuery(roomToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("The room doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(RoomDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoomServiceInsert {
        @Test
        void roomInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> roomToInsert = new HashMap<>();
            roomToInsert.put("id", 1);
            when(daoHelper.insert(any(RoomDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomInsert(roomToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(RoomDao.class), anyMap());
            assertEquals("Successful room insertion", result.getMessage());
        }

        @Test
        void roomInsertTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> roomToInsert = new HashMap<>();
            roomToInsert.put("id", 1);
            when(daoHelper.insert(any(RoomDao.class), anyMap())).thenThrow(new RuntimeException("Error"));
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomInsert(roomToInsert);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).insert(any(RoomDao.class), anyMap());
            assertNotEquals("Successful room insertion", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoomServiceUpdate {
        @Test
        void roomUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> roomToUpdate = new HashMap<>();
            roomToUpdate.put("id", 1);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenReturn(er);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomUpdate(roomToUpdate, roomToUpdate);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(RoomDao.class), anyMap(), anyMap());
            assertEquals("Successful room update", result.getMessage());
        }

        @Test
        void roomUpdateTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> roomToUpdate = new HashMap<>();
            roomToUpdate.put("id", 1);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenThrow(new RuntimeException("Error"));
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomUpdate(roomToUpdate, roomToUpdate);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).update(any(RoomDao.class), anyMap(), anyMap());
            assertNotEquals("Successful room update", result.getMessage());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoomServiceDelete {
        @Test
        void roomDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> roomKey = new HashMap<>();
            roomKey.put("id", 1);
            when(daoHelper.delete(any(RoomDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomDelete(roomKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(RoomDao.class), anyMap());
            assertEquals("Successful room delete", result.getMessage());
        }

        @Test
        void roomDeleteTestRoomNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            Map<String, Object> roomKey = new HashMap<>();
            roomKey.put("id", 1);
            when(daoHelper.delete(any(RoomDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomDelete(roomKey);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).delete(any(RoomDao.class), anyMap());
            assertEquals("Room not found", result.getMessage());
        }
    }
}